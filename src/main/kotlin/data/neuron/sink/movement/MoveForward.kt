package data.neuron.sink.movement

import data.neuron.NeuronCategory
import data.neuron.OutputNeuron

class MoveForward(
    id: String = "Mf",
    category: NeuronCategory = NeuronCategory.Sink(
        subCategory = NeuronCategory.SinkSubCategory.Movement
    )
) : OutputNeuron(id, category)