package data.neuron.sensor.entity

import data.entity.Entity
import data.entity.getLeft
import data.entity.getRight
import data.neuron.ActivationGroup
import data.neuron.NeuronCategory
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EntityRight(
    id: String = "Er",
    category: SensorCategory = SensorCategory.Entity,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG50) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        val isEntityRight = entity.fieldOfView.getRight().any { it?.hasEntity == true }
        value = 0f
    }
}
