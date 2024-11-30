package data.neuron.sensor.entity

import data.entity.Entity
import data.entity.getImmediateFront
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EntityImmediateFront(
    id: String = "Eif",
    category: SensorCategory = SensorCategory.Entity,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Float {
        value = if (entity.fieldOfView.getImmediateFront()?.hasEntity == true) 4.0f else 0.0f
        return value
    }
}
