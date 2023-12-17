package data.neuron.entity

import data.entity.Entity
import data.entity.getImmediateLeft
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class EntityImmediateLeft(
    override var value: Boolean = false,
    id: String = "Eil",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getImmediateLeft()?.hasEntity ?: false
        }
        return value
    }
}