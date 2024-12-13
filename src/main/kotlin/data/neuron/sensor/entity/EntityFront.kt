package data.neuron.sensor.entity

import data.entity.Entity
import data.entity.getFront
import data.neuron.ActivationGroup
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EntityFront(
    id: String = "Ef",
    category: SensorCategory = SensorCategory.Entity,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG28) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        val isEntityInFront = entity.fieldOfView.getFront().any { it?.hasEntity == true }
        value = 0f
    }
}
