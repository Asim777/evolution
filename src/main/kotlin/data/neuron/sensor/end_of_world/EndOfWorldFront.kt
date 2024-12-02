package data.neuron.sensor.end_of_world

import data.entity.Entity
import data.neuron.RelativeDirection
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EndOfWorldFront(
    id: String = "EoWf",
    category: SensorCategory = SensorCategory.EndOfWorld,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        if (
            SensorCategory.EndOfWorld.isEndOfWorld(
                RelativeDirection.Front,
                entity.direction,
                entity.coordinates,
                worldSize
            )
        ) {
            val distanceToEndOfWorld = SensorCategory.EndOfWorld.getDistanceToEndOfWorld(entity, worldSize)
            value = 1 / distanceToEndOfWorld
        }
    }
}