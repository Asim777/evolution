package data

data class WorldParams(
    // W
    val worldSize: Int,
    // E
    val initialPopulation: Int,
    // G
    val genomeLength: Int,
    // F
    val foodAvailability: Float,
    val mutationRate: Float,
    // N_n
    val numberOfNeurons: NumberOfNeurons
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