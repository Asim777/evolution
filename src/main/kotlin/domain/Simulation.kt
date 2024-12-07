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
    var timeElapsed : MutableState<Int> = mutableStateOf(0)
    var numberOfSpecies : MutableState<Int> = mutableStateOf(0)
    var population : MutableState<Int> = mutableStateOf(0)
    var numberOfFood : MutableState<Int> = mutableStateOf(0)
    // TODO: Update from user input
    var simulationSpeed : MutableState<SimulationSpeed> = mutableStateOf(SimulationSpeed.Normal)

    // State for entities and selected item
    var selectedEntityList : MutableState<List<Entity>> = mutableStateOf(emptyList())
    var selectedEntity : MutableState<Entity?> = mutableStateOf(null)

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

        val sensorNeurons = neurons.filter { it.category is SensorCategory }
        val innerNeurons = neurons.filter { it.category is InnerCategory }
        val sinkNeurons = neurons.filter { it.category is SinkCategory }

        // Create Gene pool
        for (i in 0 until simulationParams.initialPopulation) {
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

        for (j in 0 until simulationParams.genomeLength) {
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
                    energy = 0,
                    hunger = 0,
                    matingDrive = 0,
                    generation = 0,
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