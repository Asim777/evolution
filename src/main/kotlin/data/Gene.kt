package data

import androidx.compose.ui.graphics.Color
import data.neuron.InputNeuron
import data.neuron.Neuron
import data.neuron.OutputNeuron

class Gene(
    val input: InputNeuron<out Any>,
    val output: OutputNeuron,
    val weight: Float
) {
    override fun hashCode() = (input.hashCode() * 0.5 + output.hashCode() * 0.5).toInt()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Gene
        return input == other.input && output == other.output
    }
}

/**
 * Generate a random color based on the genes.
 * We need it to visually represent the diversity and genetic similarity
 *
 * @return generated Color
 */
fun Array<Gene>.generateColor(): Color {
    val genomeHashCode = this.contentHashCode()
    return Color(genomeHashCode and 0xFFFFFF)
}