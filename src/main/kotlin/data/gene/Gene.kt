package data.gene

import data.neuron.InputNeuron
import data.neuron.Neuron
import data.neuron.OutputNeuron

class Gene(
    val sensor: InputNeuron<out Any>,
    val inner: Neuron,
    val sink: OutputNeuron,
    val weight: Float
) {
    override fun hashCode() = sensor.hashCode() + inner.hashCode() + sink.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Gene
        return sensor == other.sensor && inner == other.inner && sink == other.sink
    }
}