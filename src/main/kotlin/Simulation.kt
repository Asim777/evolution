import data.*
import data.entity.*
import data.gene.Gene
import data.gene.Genome
import data.gene.NeuronConnection
import data.neuron.*
import data.random.RandomDataProvider
import data.random.RandomDataProviderImpl
import kotlin.collections.HashMap
import kotlin.math.pow

class Simulation(private val worldParams: WorldParams) {

    private val world = hashMapOf<Int, HashMap<Int, Cell>>()
    private val genePool = mutableListOf<Genome>()
    private val entities = mutableListOf<Entity>()
    private val randomDataProvider: RandomDataProvider by lazy { RandomDataProviderImpl() }

    /**
     * Set up the simulation
     */
    fun setup() {
        createWorld()
        createInitialGenePool()
        /*placeInitialEntities()
        placeInitialFood()*/

        randomDataProvider.reset()

        //TODO: For logging purposes only. Delete lines below when we have visual output
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

    /**
     * Start the simulation for given number of step
     */
    fun start() {
        //TODO: Run this in a loop for given number of steps
        entities.forEach { entity ->
            entity.calculateFieldOfView(world)
            entity.evaluateInputData(worldParams.worldSize)
            /*entity.calculateOutput()*/
            entity.performAction()
            println("Run finished for entity: " + entity.id)
        }
        println("Run finished")
    }

    //<editor-fold desc="Simulation Setup">
    /**
     * Creates a 2D grid of Cells that represents the World where the Entities will live
     */
    private fun createWorld() {
        for (column in 0 until worldParams.worldSize) {
            world[column] = hashMapOf()
            for (row in 0 until worldParams.worldSize) {
                world[column]?.set(
                    key = row,
                    value = Cell(
                        id = column + row, coordinates = Coordinates(column, row), hasEntity = false, hasFood = false
                    )
                ) ?: throw IllegalStateException("Cell column is null")
            }
        }
        println("World is created. Size: ${world.size*world.size} cells")
    }

    /**
     * This code snippet is a function that creates an initial gene pool. It starts by creating different types of
     * neurons (sensor, inner, sink) and then generates connections between these neurons to form genes. These genes
     * are used to create genomes, which are then added to the gene pool.
     */
    private fun createInitialGenePool() {
        // Create Neurons
        val neurons = getNeurons(worldParams.numberOfNeurons.total)

        val sensorNeurons = neurons.filter { it.category is SensorCategory }
        val innerNeurons = neurons.filter { it.category is InnerCategory }
        val sinkNeurons = neurons.filter { it.category is SinkCategory }

        // Create Gene pool
        for (i in 0 until worldParams.initialPopulation) {
            val neuronConnections = mutableListOf<NeuronConnection>()
            val genes = mutableListOf<Gene>()
            // TODO: Implement weight logic
            val weight = randomDataProvider.getRandomFloat(2)

            for (j in 0 until worldParams.genomeLength) {
                // Create a NeuronConnection and assign to the genome
                val inputList = sensorNeurons + innerNeurons
                var outputList = sinkNeurons

                val input = inputList[randomDataProvider.getRandomInteger(inputList.size)]
                // If the input is a sensor, we can connect to both inner and sink neurons, otherwise, if input is
                // inner, then we connect only to sink neurons
                if (input.category is SensorCategory) {
                    outputList = outputList + innerNeurons
                }
                val output = outputList[randomDataProvider.getRandomInteger(outputList.size)]

                if (input is InputNeuron && output is OutputNeuron) {
                    neuronConnections.add(
                        NeuronConnection(
                            input = input,
                            output = output
                        )
                    )
                }
            }

            // TODO: Continue from here. Generate genes based on NeuronConnections

            val genome = Genome(genes.toTypedArray(), neuronConnections.toTypedArray())

            // Assign the genome to the Gene pool
            genePool.add(genome)
            println("Genome created $genome for entity: $i")
        }

        println("Initial gene pool created. Size: ${genePool.size}")
    }

    /**
     * Generates initial entities for Simulation setup and places them in the world
     */
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
                    hunger = 100,
                    matingDrive =100
                )
            )
            // Update world with new entity cell coordinate
            coord.getCell()?.hasEntity = true
        }

        println("Initial entities placed in the World. Number of entities: ${entities.size}")
    }

    /**
     * Generates initial food for Simulation setup and places it in the world
     */
    private fun placeInitialFood() {
        val numberOfFood =
            (FOOD_AVAILABILITY_COEFFICIENT * worldParams.foodAvailability * worldParams.worldSize.toDouble().pow(2.0))
                .toInt()
        placeFood(numberOfFood)

        println("Initial food placed in the World. Number of food: $numberOfFood")
    }
    //</editor-fold>

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
