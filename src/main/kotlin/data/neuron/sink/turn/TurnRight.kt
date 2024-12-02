package data.neuron.sink.turn

import data.neuron.InputNeuron
import data.neuron.SinkCategory
import data.neuron.SinkNeuron

class TurnRight(
    id: String = "Tr",
    category: SinkCategory = SinkCategory.Turn,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources)
