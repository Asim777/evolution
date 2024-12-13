package data.neuron.sink.turn

import data.neuron.ActivationGroup
import data.neuron.InputNeuron
import data.neuron.SinkCategory
import data.neuron.SinkNeuron

class TurnLeft(
    id: String = "Tl",
    category: SinkCategory = SinkCategory.Turn,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources, ActivationGroup.AG9)