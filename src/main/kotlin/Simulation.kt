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
        placeInitialEntities()
        placeInitialFood()

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
        println("World is created. Size: ${world.size * world.size} cells")
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
            val neuronConnections = getNeuronConnections(sensorNeurons, innerNeurons, sinkNeurons)
            val genes = mutableListOf<Gene>()

            // TODO: Implement weight logic
            val weight = randomDataProvider.getRandomFloat(2)

            neuronConnections.forEach { connection ->

                // If we have a direct connection between a Sensor and Sink, we immediately go ahead and create the Gene
                if (connection.input is SensorNeuron && connection.output is SinkNeuron) {
                    val directConnectionGene = getNewGeneForDirectConnection(connection, genes, weight)
                    if (directConnectionGene != null) genes.add(directConnectionGene)
                }

                // If it is a connection between Sensor and Inner
                if (connection.input is SensorNeuron && connection.output is InnerNeuron) {
                    // We try to find if there is another Gene connecting this Inner Neuron to some Sink. If we
                    // find it, this is our Gene
                    val existingGene = getExistingGeneForSensorInnerConnection(connection, neuronConnections, genes)
                    if (existingGene != null) {
                        genes.remove(existingGene)
                        genes.add(existingGene.copy(sensors = existingGene.sensors + connection.input))
                    } else {
                        // If there is not existing Gene connecting this Inner Neuron to some Sink, we look if there is
                        // some unused connection that we can use to create a new Gene
                        neuronConnections.find { it.input == connection.output }?.let { complementaryConnection ->
                            genes.add(
                                Gene(
                                    sensors = listOf(connection.input),
                                    inner = connection.output,
                                    sink = complementaryConnection.output,
                                    weight = weight
                                )
                            )
                        }
                    }
                }

                // If it is a connection between Inner and Sink
                if (connection.input is InnerNeuron && connection.output is SinkNeuron) {

                    // We try to find if there is another Gene connecting this Inner Neuron to some Sensor. If
                    // we find it, we create a new Gene adding existing Gene's inputs as Sensors feeding our
                    // Inner-Sink connection
                    val existingGene = genes.find { it.inner == connection.input }
                    if (existingGene != null) {
                        genes.add(
                            Gene(
                                sensors = existingGene.sensors,
                                inner = connection.input,
                                sink = connection.output,
                                weight = weight
                            )
                        )
                    } else {
                        // If there is no Gene connecting this Inner Neuron to any Sensor, we look for any unused
                        // Connection doing the same
                        neuronConnections.find { it.output == connection.input }?.let { complementaryConnection ->
                            genes.add(
                                Gene(
                                    sensors = listOf(complementaryConnection.input),
                                    inner = connection.input,
                                    sink = connection.output,
                                    weight = weight
                                )
                            )
                        }
                    }
                }
            }

            val genome = Genome(genes.toTypedArray(), neuronConnections.toTypedArray())

            // Assign the genome to the Gene pool
            genePool.add(genome)
            println("Genome created $genome for entity: $i")
        }

        println("Initial gene pool created. Size: ${genePool.size}")
    }

    private fun getNeuronConnections(
        sensorNeurons: List<Neuron>,
        innerNeurons: List<Neuron>,
        sinkNeurons: List<Neuron>,
    ): MutableList<NeuronConnection> {
        val neuronConnections = mutableListOf<NeuronConnection>()

        for (j in 0 until worldParams.genomeLength) {
            // Create a NeuronConnection and assign to the genome
            val inputList = sensorNeurons + innerNeurons
            var outputList = sinkNeurons

            val input = inputList[randomDataProvider.getRandomInteger(inputList.size)]
            // If the input is a sensor, we can connect to both inner and sink neurons, otherwise, if input is
            // inner, then we connect only to sink neurons
            if (input is SensorNeuron) {
                outputList = outputList + innerNeurons
            }
            val output = outputList[randomDataProvider.getRandomInteger(outputList.size)]

            if (input is InputNeuron && output is OutputNeuron) {
                neuronConnections.add(
                    NeuronConnection(input = input, output = output)
                )
            }
        }
        return neuronConnections
    }

    private fun getNewGeneForDirectConnection(
        neuronConnection: NeuronConnection,
        genes: List<Gene>,
        weight: Float
    ): Gene? {
        // TODO: This shouldn't be possible to being with. NeuronConnections should not repeat
        // If there is an existing Gene connecting the same Sensor and Sink we don't create a new Gene
        // TODO: Give it more thought. Maybe we should keep a Gene with most potential instead
        if (
            genes.any { gene ->
                gene.sensors.any { it == neuronConnection.input } && gene.sink == neuronConnection.output
            }
        ) return null

        return Gene(
            sensors = listOf(neuronConnection.input),
            inner = null,
            sink = neuronConnection.output,
            weight = weight
        )
    }

    private fun getExistingGeneForSensorInnerConnection(
        neuronConnection: NeuronConnection,
        neuronConnections: MutableList<NeuronConnection>,
        genes: MutableList<Gene>
    ): Gene? {
        var existingGene: Gene? = null
        // If we already have any Gene connecting the same Sensor and Inner Neurons, we don't create a new Gene
        if (
            genes.any { gene ->
                gene.sensors.any { it == neuronConnection.input } && gene.inner == neuronConnection.output
            }
        ) {
            return null
        }

        neuronConnections
            .find { it.input == neuronConnection.output }
            ?.let { adjacentConnection: NeuronConnection ->
                // We try to find an existing Gene that has the same Inner and Sink Neurons, but gets fed by
                // a  different Sensor. In this case we don't want to create a new Gene, but to add this
                // Connection to the existing Gene
                existingGene = genes.find {
                    it.inner == neuronConnection.output && it.sink == adjacentConnection.output
                }
            }
        return existingGene
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
                    energy = 0,
                    hunger = 0,
                    matingDrive = 0,
                    generation = 0,
                    ancestralMutationCount = 0
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
                if (!hasFood && !hasEntity) hasFood = true else numberOfFoodLeftToPlaceInEnd++
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
