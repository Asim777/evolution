package data.neuron.sink.eat

import data.neuron.*

class Eat(
    id: String = "E",
    category: SinkCategory = SinkCategory.Eat,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources, ActivationGroup.AG9)
