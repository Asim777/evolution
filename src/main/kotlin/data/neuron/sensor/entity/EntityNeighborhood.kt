package data.neuron.sensor.entity

import data.entity.Entity
import data.entity.getBehind
import data.entity.getFront
import data.entity.getNeighborhood
import data.neuron.ActivationGroup
import data.neuron.NeuronCategory
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EntityNeighborhood(
    id: String = "En",
    category: SensorCategory = SensorCategory.Entity,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG57) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        val isEntityNeighborhood = entity.fieldOfView.getNeighborhood().any { it?.hasEntity == true }
        value = 0f
    }
}
