package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getRight
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodRight(
    override var value: Boolean = false,
    id: String = "Fr",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Food
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getRight().any { it?.hasFood == true }
        }
        return value
    }
}