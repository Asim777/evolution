package data.gene

import androidx.compose.ui.graphics.Color

data class Genome (
    val connections: Array<NeuronConnection>,
    val genes: Array<Gene>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Genome

        if (!connections.contentEquals(other.connections)) return false
        if (!genes.contentEquals(other.genes)) return false

        return true
    }

    override fun hashCode(): Int = genes.map { it.hashCode() }.average().toInt()


    /**
     * Generate a random color based on the genome.
     * We need it to visually represent the diversity and genetic similarity
     *
     * @return generated Color
     */
    fun generateColor(): Color {
        return Color( this.hashCode() and 0xFFFFFF)
    }
}
