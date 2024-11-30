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
)