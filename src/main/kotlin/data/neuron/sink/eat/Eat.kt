package data.neuron.sink.eat

import data.neuron.NeuronCategory
import data.neuron.OutputNeuron

class Eat(
    id: String = "E",
    category: NeuronCategory = NeuronCategory.Sink(
        subCategory = NeuronCategory.SinkSubCategory.Eat
    )
) : OutputNeuron(id, category)