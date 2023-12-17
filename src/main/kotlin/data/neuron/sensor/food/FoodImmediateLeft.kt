package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getImmediateLeft
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodImmediateLeft(
    override var value: Boolean = false,
    id: String = "Fil",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getImmediateLeft()?.hasFood ?: false
        }
        return value
    }
}