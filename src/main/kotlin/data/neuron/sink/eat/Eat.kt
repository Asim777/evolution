package data.neuron.sink.eat

import data.entity.Entity
import data.entity.getSameLocation
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class Eat(
    override var value: Boolean = false,
    id: String = "E",
    category: NeuronCategory = NeuronCategory.Sink(
        subCategory = NeuronCategory.SinkSubCategory.Eat
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = entity.fieldOfView.getSameLocation()?.hasFood ?: false
        return value
    }
}