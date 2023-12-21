package data.gene

import data.neuron.InputNeuron
import data.neuron.OutputNeuron

data class NeuronConnection(
    val input: InputNeuron<out Any>,
    val output: OutputNeuron,
)