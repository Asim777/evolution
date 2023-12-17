package data.neuron.sink.turn

import data.entity.*
import data.neuron.NeuronCategory
import data.neuron.ValuelessNeuron

class TurnRight(
    id: String = "Tr",
    category: NeuronCategory = NeuronCategory.Sink(
        subCategory = NeuronCategory.SinkSubCategory.Turn
    )
) : ValuelessNeuron(id, category) {
    override fun evaluate(entity: Entity, worldSize: Int) {
        // This neuron doesn't need any evaluation. Entity always can turn in any direction
    }
}