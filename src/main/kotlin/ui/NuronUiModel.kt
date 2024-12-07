package ui

data class NeuronUiModel(
    val name: String,
    val isActive: Boolean,
    var center: NeuronCoordinateUiModel? = null
)

data class NeuronCoordinateUiModel(
    val x: Float,
    val y: Float
)

fun getUiSensorNeurons() = listOf(
    NeuronUiModel("DO", false),
    NeuronUiModel("DEoW", false),
    NeuronUiModel("DE", false),
    NeuronUiModel("DF", false),
    NeuronUiModel("Fa", false),
    NeuronUiModel("GSa", false),
    NeuronUiModel("EDa", false),
    NeuronUiModel("Eif", true),
    NeuronUiModel("Fif", true),
    NeuronUiModel("EoWf", true),
    NeuronUiModel("Ef", false),
    NeuronUiModel("Ff", false),
    NeuronUiModel("GSf", false),
    NeuronUiModel("EDf", false),
    NeuronUiModel("Eil", false),
    NeuronUiModel("Fil", false),
    NeuronUiModel("EoWl", false),
    NeuronUiModel("El", false),
    NeuronUiModel("Fl", false),
    NeuronUiModel("GSl", false),
    NeuronUiModel("Eir", false),
    NeuronUiModel("Fir", false),
    NeuronUiModel("EoWr", false),
    NeuronUiModel("Er", false),
    NeuronUiModel("Fr", false),
    NeuronUiModel("GSr", false),
    NeuronUiModel("Eb", false),
    NeuronUiModel("Fb", false),
    NeuronUiModel("GSb", false),
    NeuronUiModel("EDb", false),
    NeuronUiModel("En", false),
    NeuronUiModel("Fn", false),
    NeuronUiModel("GSn", false),
    NeuronUiModel("EDn", false)
)

fun getUiInnerNeurons() = listOf(
    NeuronUiModel("!", false),
    NeuronUiModel("&", false),
    NeuronUiModel("|", false),
    NeuronUiModel("^", false),
    NeuronUiModel("<0.1", false),
    NeuronUiModel("<0.25", false),
    NeuronUiModel("<0.5", false),
    NeuronUiModel("<0.75", false),
    NeuronUiModel(">0.25", false),
    NeuronUiModel(">0.5", false),
    NeuronUiModel(">0.75", false),
    NeuronUiModel(">0.9", false)
)

fun getUiSinkNeurons() = listOf(
    NeuronUiModel("M", true),
    NeuronUiModel("E", true),
    NeuronUiModel("Mf", true),
    NeuronUiModel("Mr", false),
    NeuronUiModel("Ml", false),
    NeuronUiModel("Mb", false),
    NeuronUiModel("Mran", false),
    NeuronUiModel("Tr", true),
    NeuronUiModel("Tl", true),
    NeuronUiModel("Tb", false)
)