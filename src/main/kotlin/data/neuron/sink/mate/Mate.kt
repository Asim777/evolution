package data.neuron.sink.mate

import data.neuron.NeuronCategory
import data.neuron.OutputNeuron

class Mate(
    id: String = "E",
    category: NeuronCategory = NeuronCategory.Sink(
        subCategory = NeuronCategory.SinkSubCategory.Eat
    )
) : OutputNeuron(id, category)