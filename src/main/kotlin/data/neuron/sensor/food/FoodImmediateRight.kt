package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getImmediateRight
import data.neuron.ActivationGroup
import data.neuron.NeuronCategory
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class FoodImmediateRight(
    id: String = "Fir",
    category: SensorCategory = SensorCategory.Food,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG19) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        value = 0f
    }
}