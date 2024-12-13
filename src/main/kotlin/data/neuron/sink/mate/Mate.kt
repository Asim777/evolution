package data.neuron.sink.mate

import data.neuron.*

class Mate(
    id: String = "M",
    category: SinkCategory = SinkCategory.Mate,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources, ActivationGroup.AG9)
