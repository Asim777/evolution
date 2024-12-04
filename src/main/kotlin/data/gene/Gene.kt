package data.gene

import data.neuron.InputNeuron
import data.neuron.Neuron
import data.neuron.OutputNeuron

/**
 * Gene is a unit of Entity's genome that stores and carries the information related to behavior of the Entity.
 * It consists of Sensor neuron, Inner neuron and Sink neuron.
 */
data class Gene(
    var sensors: List<InputNeuron>,
    val inner: Neuron?,
    val sink: OutputNeuron,
    // TODO: Figure out how to use the weight
    val weight: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Gene

        if (weight != other.weight) return false
        if (sensors != other.sensors) return false
        if (inner != other.inner) return false
        if (sink != other.sink) return false

        return true
    }

    override fun hashCode(): Int {
        var result = weight.hashCode()
        result = 31 * result + sensors.hashCode()
        result = 31 * result + (inner?.hashCode() ?: 0)
        result = 31 * result + sink.hashCode()
        return result
    }
}