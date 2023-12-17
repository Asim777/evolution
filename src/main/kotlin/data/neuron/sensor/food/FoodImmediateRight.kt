package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getImmediateRight
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodImmediateRight(
    override var value: Boolean = false,
    id: String = "Fir",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Food
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getImmediateRight()?.hasFood ?: false
        }
        return value
    }
}