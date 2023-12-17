import data.*
import data.entity.*
import data.neuron.NeuronCategory
import data.neuron.getNeurons
import data.random.RandomDataProvider
import data.random.RandomDataProviderImpl
import kotlin.collections.HashMap
import kotlin.math.pow

class Simulation(private val worldParams: WorldParams) {

    private val world = hashMapOf<Int, HashMap<Int, Cell>>()
    private val genePool = mutableListOf<Array<Gene>>()
    private val entities = mutableListOf<Entity>()
    private val randomDataProvider: RandomDataProvider by lazy { RandomDataProviderImpl() }

    fun setup() {
        createWorld()
        createInitialGenePool()
        placeInitialEntities()
        placeInitialFood()

        randomDataProvider.reset()

        //TODO: Delete lines below
        val worldColumns = world.values
        val numberOfFood = worldColumns.flatMap {
            it.values.filter { cell -> cell.hasFood }
        }.size
        val numberOfEntities = worldColumns.flatMap {
            it.values.filter { cell -> cell.hasEntity }
        }.size
        val numberOfCellsWithEntitiesAndFood = worldColumns.flatMap {
            it.values.filter { cell -> cell.hasFood && cell.hasEntity }
        }.size
        println(
            "Setup Finished, " +
                    "number of entities: $numberOfEntities, " +
                    "number of food: $numberOfFood, " +
                    "number of cells with entities and food: $numberOfCellsWithEntitiesAndFood"
        )
    }

    fun start() {
        entities.forEach {entity ->
            entity.calculateFieldOfView(world)
            entity.evaluateInputData(worldParams.worldSize)
            entity.calculateOutput()
            entity.performAction()
            println("Run finished for entity: " + entity.id)
        }
        println("Run finished")
    }

    private fun createWorld() {
        for (column in 0 until worldParams.worldSize) {
            world[column] = hashMapOf()
            for (row in 0 until worldParams.worldSize) {
                world[column]?.set(
                    key = row, value = Cell(
                        id = column + row, coordinates = Coordinates(column, row), hasEntity = false, hasFood = false
                    )
                ) ?: throw IllegalStateException("Cell column is null")
            }
        }
    }

    private fun createInitialGenePool() {
        // Create Neurons
        val neurons = getNeurons(worldParams.numberOfNeurons.total)

        val sensorNeurons = neurons.filter { it.category is NeuronCategory.Sensor }
        val innerNeurons = neurons.filter { it.category is NeuronCategory.Inner }
        val sinkNeurons = neurons.filter { it.category is NeuronCategory.Sink }

        // Create Gene pool
        for (i in 0 until worldParams.initialPopulation) {
            val genome = mutableListOf<Gene>()

            for (j in 0 until worldParams.genomeLength) {
                // Create a gene and assign to the genome
                genome.add(
                    j,
                    Gene(
                        input = sensorNeurons.plus(innerNeurons).run {
                            get(randomDataProvider.getRandomInteger(size))
                        },
                        output = sinkNeurons.plus(innerNeurons).run {
                            get(randomDataProvider.getRandomInteger(size))
                        },
                        weight = randomDataProvider.getRandomFloat(2)
                    )
                )
            }

            // Assign the genome to the Gene pool
            genePool.add(i, genome.toTypedArray())
        }
    }

    private fun placeInitialEntities() {
        for (i in 0 until worldParams.initialPopulation) {
            val genome = genePool[i]
            val coord = getRandomNonOccupiedCoordinate()
            entities.add(
                Entity(
                    id = i,
                    genome = genome,
                    color = genome.generateColor(),
                    coordinates = coord,
                    direction = Direction.values()[randomDataProvider.getRandomInteger(4)],
                    fieldOfView = FieldOfView(),
                    age = 0,
                    energy = 100,
                    hunger = 0,
                    sexualDrive = 0
                )
            )
            // Update world with new entity cell coordinate
            coord.getCell()?.hasEntity = true
        }
    }

    /**
     * Generates initial food for Simulation setup and places it in the world
     */
    private fun placeInitialFood() {
        val numberOfFood =
            (FOOD_AVAILABILITY_COEFFICIENT * worldParams.foodAvailability * worldParams.worldSize.toDouble().pow(2.0))
                .toInt()
        placeFood(numberOfFood)
    }

    /**
     * Places a given number of food in the world at random locations.
     * Calls itself as many times as needed until all the given amount of food is placed
     *
     * @param numberOfFoodToPlace - number of food that needs to be placed on the World
     */
    private fun placeFood(numberOfFoodToPlace: Int) {
        var numberOfFoodLeftToPlaceInEnd = 0
        val foodLocations = (0 until worldParams.worldSize.toDouble().pow(2).toInt())
            .shuffled()
            .take(numberOfFoodToPlace)

        for (i in 0 until numberOfFoodToPlace) {
            val coord = Coordinates(
                x = foodLocations[i] % worldParams.worldSize, y = foodLocations[i] / worldParams.worldSize
            )
            coord.getCell()?.run {
                if (!hasFood) hasFood = true else numberOfFoodLeftToPlaceInEnd++
            }
        }

        if (numberOfFoodLeftToPlaceInEnd > 0) placeFood(numberOfFoodLeftToPlaceInEnd)
    }

    /**
     * Returns a random coordinate in the World that is not currently occupied by any entity
     *
     * @return Coordinates - random non-occupied coordinate
     */
    private fun getRandomNonOccupiedCoordinate(): Coordinates {
        var coord: Coordinates
        do {
            coord = Coordinates(
                x = randomDataProvider.getRandomInteger(worldParams.worldSize),
                y = randomDataProvider.getRandomInteger(worldParams.worldSize)
            )
        } while (coord.getCell()?.hasEntity == true)
        return coord
    }

    private fun Coordinates.getCell() = world[this.x]?.get(this.y)

    companion object {
        private const val FOOD_AVAILABILITY_COEFFICIENT = 0.4
    }
}
