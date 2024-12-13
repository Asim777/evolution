package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getBehind
import data.entity.getImmediateFront
import data.neuron.ActivationGroup
import data.neuron.NeuronCategory
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class FoodAhead(
    id: String = "Fa",
    category: SensorCategory = SensorCategory.Food,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG28) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        value = 0f
    }
}
