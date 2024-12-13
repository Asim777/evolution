package data.gene

import data.neuron.InputNeuron
import data.neuron.OutputNeuron

/**
 * NeuronConnection is a relationship between two Neurons - Input and Output
 *
 * @property input InputNeuron : The Neuron that provides some information. It can be either Sensor or Input Inner Neuron
 * @property output InputNeuron : The Neuron that receives some information. It can be either Sink or Output Inner Neuron
 */
data class NeuronConnection(
    val input: InputNeuron,
    val output: OutputNeuron
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NeuronConnection

        if (input != other.input) return false
        if (output != other.output) return false

        return true
    }

    override fun hashCode(): Int {
        var result = input.hashCode()
        result = 31 * result + output.hashCode()
        return result
    }
}

