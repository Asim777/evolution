package data.neuron.sensor.end_of_world

import data.entity.Entity
import data.neuron.ActivationGroup
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EndOfWorldBehind(
    id: String = "EoWb",
    category: SensorCategory = SensorCategory.EndOfWorld,
    override var value: Float = 0.0f,
) : SensorNeuron(id, category, value,ActivationGroup.AG57) {
    override fun evaluate(entity: Entity, worldSize: Int) {
        value = 0f
    }
}
