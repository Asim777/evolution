package data.gene

import androidx.compose.ui.graphics.Color

/**
 * Genome represents the genetic information of one [Entity]. It's assigned at creation and can be changed only as a
 * result of random mutation
 *
 * @property genes Array<Gene>: array of [Gene]s that the [Entity] has
 * @property connections Array<NeuronConnection>: array of [NeuronConnection]s that the [Entity] has
 */
data class Genome (
    val genes: Array<Gene>,
    val connections: Array<NeuronConnection>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Genome

        if (!genes.contentEquals(other.genes)) return false
        if (!connections.contentEquals(other.connections)) return false

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
