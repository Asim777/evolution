package data.neuron.sensor.entity_density

import data.entity.Entity
import data.neuron.ActivationGroup
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EntityDensityNeighborhood(
    id: String = "EDn",
    category: SensorCategory = SensorCategory.Entity,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG35) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        value = 0f
    }
}
