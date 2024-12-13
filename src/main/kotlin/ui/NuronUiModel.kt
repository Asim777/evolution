package ui

import androidx.compose.ui.geometry.Offset

data class NeuronUiModel(
    val name: String,
    val explanation: String,
    val isActive: Boolean,
    var center: Offset? = null,
    val excitementValue: Float = 0f
)

fun getUiSensorNeurons() = listOf(
    NeuronUiModel("DO", "Distance to Object", false),
    NeuronUiModel("DEoW", "Distance to End of World", false),
    NeuronUiModel("DE", "Distance to Entity", false),
    NeuronUiModel("DF", "Distance to Food", false),
    NeuronUiModel("Fa", "Food ahead", false),
    NeuronUiModel("GSa", "Genetic Similarity ahead", false),
    NeuronUiModel("EDa", "Entity Density ahead", false),
    NeuronUiModel("Eif", "Entity immediately in front", true),
    NeuronUiModel("Fif", "Food immediately in front", true),
    NeuronUiModel("EoWf", "End of World in front", true),
    NeuronUiModel("Ef", "Entity in front",  false),
    NeuronUiModel("Ff", "Food in front", false),
    NeuronUiModel("GSf","Genetic Similarity in front", false),
    NeuronUiModel("EDf", "Entity Density in front", false),
    NeuronUiModel("Eil", "Entity immediately to left", false),
    NeuronUiModel("Fil", "Food immediately to left", false),
    NeuronUiModel("EoWl", "End of World to left", false),
    NeuronUiModel("El", "Entity to left", false),
    NeuronUiModel("Fl", "Food to left", false),
    NeuronUiModel("GSl", "Genetic similarity to left", false),
    NeuronUiModel("Eir", "Entity immediately to right", false),
    NeuronUiModel("Fir", "Food immediately to right", false),
    NeuronUiModel("EoWr", "End of World to right", false),
    NeuronUiModel("Er", "Entity to right", false),
    NeuronUiModel("Fr", "Food to right", false),
    NeuronUiModel("GSr", "Genetic Similarity to right", false),
    NeuronUiModel("Eb", "Entity behind", false),
    NeuronUiModel("Fb", "Food behind", false),
    NeuronUiModel("GSb", "Genetic Similarity behind" , false),
    NeuronUiModel("EDb", "Entity Density behind", false),
    NeuronUiModel("En", "Entity in neighborhood", false),
    NeuronUiModel("Fn", "Food in neighborhood", false),
    NeuronUiModel("GSn", "Genetic Similarity in neighborhood", false),
    NeuronUiModel("EDn", "Entity Density in neighborhood", false)
)

fun getUiInnerNeurons() = listOf(
    NeuronUiModel("!", "Not", false),
    NeuronUiModel("&", "And", false),
    NeuronUiModel("|", "Or", false),
    NeuronUiModel("^", "Xor", false),
    NeuronUiModel("<0.1", "Less Than 0.1", false),
    NeuronUiModel("<0.25", "Less Than 0.25",false),
    NeuronUiModel("<0.5", "Less Than 0.5",false),
    NeuronUiModel("<0.75", "Less Than 0.75",false),
    NeuronUiModel(">0.25", "More than 0.25", false),
    NeuronUiModel(">0.5", "More than 0.5", false),
    NeuronUiModel(">0.75", "More than 0.75", false),
    NeuronUiModel(">0.9", "More than 0.9", false)
)

fun getUiSinkNeurons() = listOf(
    NeuronUiModel("M", "Mate", true),
    NeuronUiModel("E", "Eat", true),
    NeuronUiModel("Mf", "Move forward", true),
    NeuronUiModel("Mr", "Move right", false),
    NeuronUiModel("Ml", "Move left", false),
    NeuronUiModel("Mb", "Move behind", false),
    NeuronUiModel("Mran", "Move randomly", false),
    NeuronUiModel("Tr", "Turn right", true),
    NeuronUiModel("Tl", "Turn left", true),
    NeuronUiModel("Tb", "Turn behind", false)
)