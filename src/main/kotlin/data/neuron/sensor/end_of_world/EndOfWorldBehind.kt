package data.neuron.sensor.end_of_world

import data.entity.Direction
import data.entity.Entity
import data.neuron.NeuronCategory
import data.neuron.NumericalInputNeuron

class EndOfWorldBehind(
    override var value: Double = -1.0,
    id: String = "EoWb",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.EndOfWorld
    )
) : NumericalInputNeuron(value, id, category) {
    override fun evaluate(entity: Entity, worldSize: Int): Double {
        value = with(entity) {
            when (direction) {
                Direction.North -> if (coordinates.y + 1 < worldSize) {
                    (worldSize - coordinates.y - 1) * 0.25
                } else -1.0
                Direction.East -> if (coordinates.x - 2 < 0) coordinates.x * 0.25 else -1.0
                Direction.South -> if (coordinates.y - 2 < 0) coordinates.y * 0.25 else -1.0
                Direction.West -> if (coordinates.x + 1 < worldSize) {
                    (worldSize - coordinates.x - 1) * 0.25
                } else -1.0
            }
        }
        return value
    }
}