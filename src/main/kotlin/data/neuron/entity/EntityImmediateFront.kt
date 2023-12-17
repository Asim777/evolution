package data.neuron.entity

import data.entity.Entity
import data.entity.getImmediateFront
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class EntityImmediateFront(
    override var value: Boolean = false,
    id: String = "Eif",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = entity.fieldOfView.getImmediateFront()?.hasEntity ?: false
        return value
    }
}