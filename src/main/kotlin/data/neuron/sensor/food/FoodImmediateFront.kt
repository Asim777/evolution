package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getImmediateFront
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodImmediateFront(
    override var value: Boolean = false,
    id: String = "Fif",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Food
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getImmediateFront()?.hasFood ?: false
        }
        return value
    }
}