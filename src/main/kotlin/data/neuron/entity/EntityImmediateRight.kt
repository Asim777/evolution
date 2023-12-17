package data.neuron.entity

import data.entity.Entity
import data.entity.getImmediateRight
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class EntityImmediateRight(
    override var value: Boolean = false,
    id: String = "Eir",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getImmediateRight()?.hasEntity ?: false
        }
        return value
    }
}