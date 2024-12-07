package data

data class SimulationParams(
    // W
    val worldSize: Int,
    // E
    val initialPopulation: Int,
    // F
    var foodAvailability: Float,
    // M_r
    var mutationRate: Float,
    // N_n
    val numberOfNeurons: NumberOfNeurons,
    // G
    val genomeLength: Int,
)

data class NumberOfNeurons(
    val total: Int,
    // N_sn
    val sensorNeurons: Int,
    // N_sn
    val innerNeurons: Int,
    // N_sk
    val sinkNeurons: Int,
)