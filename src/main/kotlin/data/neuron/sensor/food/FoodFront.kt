package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getFront
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodFront(
    override var value: Boolean = false,
    id: String = "Ff",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Food
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getFront().any { it?.hasFood == true }
        }
        return value
    }
}