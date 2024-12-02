package data.neuron.sensor.end_of_world

import data.entity.Entity
import data.neuron.RelativeDirection
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

/*
class EndOfWorldLeft(
    id: String = "EoWl",
    category: SensorCategory = SensorCategory.EndOfWorld,
    override var value: Float = -1.0f
) : SensorNeuron<Float>(id, category, value) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Float =
        if (SensorCategory.EndOfWorld.isEndOfWorld(
                RelativeDirection.Left,
                entity.direction,
                entity.coordinates,
                worldSize
            )
        ) {
            entity.coordinates.y * 0.25f
        } else -1.0f
}
*/
