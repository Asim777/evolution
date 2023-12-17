package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getLeft
import data.neuron.LogicalInputNeuron
import data.neuron.NeuronCategory

class FoodLeft(
    override var value: Boolean = false,
    id: String = "Fl",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : LogicalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        value = with(entity) {
            fieldOfView.getLeft().any { it?.hasFood == true }
        }
        return value
    }
}