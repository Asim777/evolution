package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getSameLocation
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodSameLocation(
    override var value: Boolean = false,
    id: String = "Fsl",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Food
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getSameLocation()?.hasFood ?: false
        }
        return value
    }
}