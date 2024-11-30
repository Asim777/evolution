package data.neuron.sensor.end_of_world

import data.entity.Entity
import data.neuron.RelativeDirection
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

/*
class EndOfWorldBehind(
    id: String = "EoWb",
    category: SensorCategory = SensorCategory.EndOfWorld,
    override var value: Boolean = false,
) : SensorNeuron<Boolean>(id, category, value, type = NeuronType.Numerical) {
    override fun evaluate(entity: Entity, worldSize: Int): Boolean =
        if (SensorCategory.EndOfWorld.isEndOfWorld(
                RelativeDirection.Behind,
                entity.direction,
                entity.coordinates,
                worldSize
            )
        ) {
            SensorCategory.EndOfWorld.getDistanceToEndOfWorld(entity, worldSize)
        } else false
}*/
