package data.neuron.entity

import data.entity.Entity
import data.entity.getBehind
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class EntityBehind(
    override var value: Boolean = false,
    id: String = "Eb",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getBehind().any { it?.hasEntity == true }
        }
        return value
    }
}