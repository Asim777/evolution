package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getBehind
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodBehind(
    override var value: Boolean = false,
    id: String = "Fb",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Food
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getBehind().any { it?.hasFood == true }
        }
        return value
    }
}