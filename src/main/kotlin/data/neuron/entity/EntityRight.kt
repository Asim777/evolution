package data.neuron.entity

import data.entity.Entity
import data.entity.getRight
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class EntityRight(
    override var value: Boolean = false,
    id: String = "Er",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getRight().any { it?.hasEntity == true }
        }
        return value
    }
}