package data.neuron.sensor.end_of_world

import data.entity.Direction
import data.entity.Entity
import data.neuron.NeuronCategory
import data.neuron.NumericalInputNeuron

class EndOfWorldRight(
    override var value: Double = -1.0,
    id: String = "EoWr",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.EndOfWorld
    )
) : NumericalInputNeuron(value, id, category) {
    override fun evaluate(entity: Entity, worldSize: Int): Double {
        value = with(entity) {
            when (direction) {
                Direction.North -> if (coordinates.x + 4 < worldSize) {
                    (worldSize - coordinates.x - 1) * 0.25
                } else -1.0
                Direction.East -> if (coordinates.y + 4 < worldSize) {
                    (worldSize - coordinates.y - 1) * 0.25
                } else -1.0
                Direction.South -> if (coordinates.x - 5 < 0) coordinates.x * 0.25 else -1.0
                Direction.West -> if (coordinates.y - 5 < 0) coordinates.y * 0.25 else -1.0
            }
        }
        return value
    }
}