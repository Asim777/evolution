package data.neuron.sink.movement

import data.neuron.ActivationGroup
import data.neuron.InputNeuron
import data.neuron.SinkCategory
import data.neuron.SinkNeuron

class MoveLeft(
    id: String = "Ml",
    category: SinkCategory = SinkCategory.Movement,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources, ActivationGroup.AG50)