package data.neuron.sink.turn

import data.neuron.ActivationGroup
import data.neuron.InputNeuron
import data.neuron.SinkCategory
import data.neuron.SinkNeuron

class TurnBack(
    id: String = "Tb",
    category: SinkCategory = SinkCategory.Turn,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources, ActivationGroup.AG35)