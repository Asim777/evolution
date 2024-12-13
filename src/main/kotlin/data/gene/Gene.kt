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
    val name: String = sensors.joinToString(",") { it.id } + "_${inner?.id ?: ""}_${sink.id}",
)

fun Gene.containsNeuron(neuronName: String) =
    sensors.any {it.id == neuronName} || inner?.id == neuronName || sink.id == neuronName

fun Gene.getNeuron(neuronName: String) : Neuron? {
    var neuron: Neuron? = sensors.find {it.id == neuronName}
    if (neuron == null && inner?.id == neuronName) {
        neuron = inner
    } else if (neuron == null && sink.id == neuronName) {
        neuron = sink
    }
    return neuron
}