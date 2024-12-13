package domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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

object Simulation : ISimulation {

    private const val FOOD_AVAILABILITY_COEFFICIENT = 0.4

    // World state
    private val world = hashMapOf<Int, HashMap<Int, Cell>>()
    private val genePool = mutableListOf<Genome>()
    private val entities = mutableListOf<Entity>()

    // Simulation stats state. Only update once at the end of every Simulation step
    var timeElapsed: MutableState<Int> = mutableStateOf(0)
    var numberOfSpecies: MutableState<Int> = mutableStateOf(0)
    var population: MutableState<Int> = mutableStateOf(0)
    var numberOfFood: MutableState<Int> = mutableStateOf(0)

    // TODO: Update from user input
    var simulationSpeed: MutableState<SimulationSpeed> = mutableStateOf(SimulationSpeed.Normal)

    // State for entities and selected item
    var selectedEntityList: MutableState<List<Entity>> = mutableStateOf(emptyList())
    var selectedEntity: MutableState<Entity?> = mutableStateOf(null)
    var selectedGene: MutableState<Gene?> = mutableStateOf(null)
    var selectedNeuron: MutableState<Neuron?> = mutableStateOf(null)

    private var simulationParams: SimulationParams = SimulationParams(
        worldSize = 1000,
        initialPopulation = 1000,
        foodAvailability = 0.5f,
        mutationRate = 0.01f,
        NumberOfNeurons(
            total = 9,
            sensorNeurons = 4,
            innerNeurons = 0,
            sinkNeurons = 5
        ),
        genomeLength = 4
    )

    private val randomDataProvider: RandomDataProvider by lazy { RandomDataProviderImpl() }

    // This fields should track number of food at the World at any given moment. Add to it when new food is placed.
    // Subtract from it when any food is eaten. It's value will be assigned to numberOfFood MutableState variable at the
    // end of each run
    private var foodCounter = 0

    /**
     * Set up the simulation
     */
    // TODO: It should run in a separate Simulation thread
    override fun setup(simulationParams: SimulationParams) {

        this.simulationParams = simulationParams

        createWorld()
        createInitialGenePool()
        placeInitialEntities()
        placeInitialFood()

        randomDataProvider.reset()

        //TODO: For logging purposes only. Delete lines below when we have visual output
        val worldColumns = world.values
        val numberOfFoodLog = worldColumns.flatMap {
            it.values.filter { cell -> cell.hasFood }
        }.size
        val numberOfEntitiesLog = worldColumns.flatMap {
            it.values.filter { cell -> cell.hasEntity }
        }.size
        val numberOfCellsWithEntitiesAndFoodLog = worldColumns.flatMap {
            it.values.filter { cell -> cell.hasFood && cell.hasEntity }
        }.size
        println(
            "Setup Finished, " +
                    "number of entities: $numberOfEntitiesLog, " +
                    "number of food: $numberOfFoodLog, " +
                    "number of cells with entities and food: $numberOfCellsWithEntitiesAndFoodLog"
        )

        // Updating state variables
        numberOfSpecies.value = calculateNumberOfSpecies()
        population.value = entities.size
        numberOfFood.value = foodCounter

        // Updating selected entities
        updateSample()
    }

    /**
     * Run the simulation for given number of steps
     */
    // TODO: It should run in a separate Simulation thread
    override fun run() {
        entities.forEach { entity ->
            entity.calculateFieldOfView(world)
            entity.evaluateInputData(simulationParams.worldSize)
            /*entity.calculateOutput()*/
            entity.performAction()
            println("Simulation step finished for entity: " + entity.id)
        }
        println("Simulation step finished")

        // Updating state variables
        timeElapsed.value++
        numberOfSpecies.value = calculateNumberOfSpecies()
        population.value = entities.size
        numberOfFood.value = foodCounter

        // Controlling the speed of the Simulation
        Thread.sleep(1000 / simulationSpeed.value.ordinal.toLong())
    }

    override fun setFoodAvailability(foodAvailability: Float) {
        simulationParams.foodAvailability = foodAvailability
    }

    override fun setMutationRate(mutationRate: Float) {
        simulationParams.mutationRate = mutationRate
    }

    override fun setSpeed(speed: SimulationSpeed) {
        simulationSpeed.value = speed
    }

    override fun onSampleClicked() {
        updateSample()
    }

    override fun getGenePool(): List<Genome> {
        return genePool.toList()
    }

    override fun getSimulationParams(): SimulationParams = simulationParams
    override fun getEntities(): List<Entity> {
        return entities
    }

    //<editor-fold desc="domain.Simulation Setup">
    /**
     * Creates a 2D grid of Cells that represents the World where the Entities will live
     */
    private fun createWorld() {
        for (column in 0 until simulationParams.worldSize) {
            world[column] = hashMapOf()
            for (row in 0 until simulationParams.worldSize) {
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
        val neurons = getNeurons(simulationParams.numberOfNeurons.total)

        val sensorNeurons = neurons.filterIsInstance<SensorNeuron>()
        val innerNeurons = neurons.filterIsInstance<InnerNeuron>()
        val sinkNeurons = neurons.filterIsInstance<SinkNeuron>()

        // Create Gene pool
        for (i in 0 until simulationParams.initialPopulation) {
            val neuronConnections = getNeuronConnections(sensorNeurons, innerNeurons, sinkNeurons)
            val genes = mutableListOf<Gene>()

            neuronConnections.forEach { connection ->

                // If we have a direct connection between a Sensor and Sink, we create a Gene and add it if it doesn't
                // already exist
                if (connection.input is SensorNeuron && connection.output is SinkNeuron) {
                    val newGene = Gene(
                        sensors = listOf(connection.input),
                        inner = null,
                        sink = connection.output
                    )

                    if (!genes.contains(newGene)) genes.add(newGene)
                }

                // If it is a connection between Sensor and Inner
                if (connection.input is SensorNeuron && connection.output is InnerNeuron) {
                    // Find all the connections between this Inner and any Sensors. We want to include all of them in
                    // one Gene
                    val allSensors = neuronConnections
                        .filter { it.input is SensorNeuron && it.output == connection.output }
                        .map { it.input }

                    // Find all the connections between this Inner and any Sinks. We want to create a separate Gene for
                    // each of them with the list of Sensors and this Inner Neuron
                    val allSinks = neuronConnections
                        .filter { it.input == connection.output && it.output is SinkNeuron }
                        .map { it.output as SinkNeuron}

                    allSinks.forEach { sink ->
                        val geneToAdd = Gene(
                            sensors = allSensors,
                            inner = connection.output,
                            sink = sink
                        )
                        if (!genes.contains(geneToAdd)) genes.add(geneToAdd)
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

        for (j in 0 until simulationParams.genomeLength) {
            val connection = generateNeuronConnection(sensorNeurons, innerNeurons, sinkNeurons, neuronConnections)
            neuronConnections.add(connection)
        }
        return neuronConnections
    }

    private fun generateNeuronConnection(
        sensorNeurons: List<Neuron>,
        innerNeurons: List<Neuron>,
        sinkNeurons: List<Neuron>,
        neuronConnections: MutableList<NeuronConnection>
    ): NeuronConnection {
        var connectionToAdd: NeuronConnection
        do {
            connectionToAdd = getRandomNeuronConnection(sensorNeurons, innerNeurons, sinkNeurons)
        } while (neuronConnections.any { it == connectionToAdd })
        return connectionToAdd
    }

    private fun getRandomNeuronConnection(
        sensorNeurons: List<Neuron>,
        innerNeurons: List<Neuron>,
        sinkNeurons: List<Neuron>
    ) : NeuronConnection {
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

        return NeuronConnection(input = input as InputNeuron, output = output as OutputNeuron)
    }

    /**
     * Generates initial entities for domain.Simulation setup and places them in the world
     */
    private fun placeInitialEntities() {
        for (i in 0 until simulationParams.initialPopulation) {
            val genome = genePool[i]
            val coordinates = getRandomNonOccupiedCoordinate()
            entities.add(
                Entity(
                    id = i,
                    genome = genome,
                    color = genome.generateColor(),
                    coordinates = coordinates,
                    direction = Direction.values()[randomDataProvider.getRandomInteger(4)],
                    fieldOfView = FieldOfView(),
                    age = 0,
                    health = 100,
                    energy = 100,
                    hunger = 0,
                    matingDrive = 0,
                    generation = 1,
                    ancestralMutationCount = 0
                )
            )
            // Update world with new entity cell coordinate
            coordinates.getCell()?.hasEntity = true
        }

        println("Initial entities placed in the World. Number of entities: ${entities.size}")
    }

    /**
     * Generates initial food for domain.Simulation setup and places it in the world
     */
    private fun placeInitialFood() {
        val numberOfFood =
            (FOOD_AVAILABILITY_COEFFICIENT * simulationParams.foodAvailability * simulationParams.worldSize.toDouble()
                .pow(2.0))
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
        val foodLocations = (0 until simulationParams.worldSize.toDouble().pow(2).toInt())
            .shuffled()
            .take(numberOfFoodToPlace)

        for (i in 0 until numberOfFoodToPlace) {
            val coordinates = Coordinates(
                x = foodLocations[i] % simulationParams.worldSize, y = foodLocations[i] / simulationParams.worldSize
            )
            coordinates.getCell()?.run {
                if (!hasFood && !hasEntity) {
                    hasFood = true
                    foodCounter++
                } else {
                    numberOfFoodLeftToPlaceInEnd++
                }
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
        var coordinates: Coordinates
        do {
            coordinates = Coordinates(
                x = randomDataProvider.getRandomInteger(simulationParams.worldSize),
                y = randomDataProvider.getRandomInteger(simulationParams.worldSize)
            )
        } while (coordinates.getCell()?.hasEntity == true)
        return coordinates
    }

    private fun Coordinates.getCell() = world[this.x]?.get(this.y)

    private fun calculateNumberOfSpecies(): Int {
        // TODO Not implemented
        return 0
    }

    /**
     * Updates the selection of 10 Selected Entities (Sample) with 10 random Entities from the current list of Entities
     */
    private fun updateSample() {
        if (entities.isEmpty()) return

        selectedEntityList.value = entities.shuffled().take(10)
        selectedEntity.value = selectedEntityList.value.firstOrNull()
    }
}

enum class SimulationSpeed(speed: Int) {
    Normal(1),
    Double(2),
    Quadruple(4),
    Octuple(8),
    x16(16)
}