package data.neuron.sink.movement

import data.neuron.*

class MoveForward(
    id: String = "Mf",
    category: SinkCategory = SinkCategory.Movement,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources, ActivationGroup.AG9)
