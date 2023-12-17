package data.neuron.entity

import data.entity.Direction
import data.entity.Entity
import data.entity.getFront
import data.neuron.NeuronCategory
import data.neuron.NumericalInputNeuron

class EntityFront(
    override var value: Double = -1.0,
    id: String = "Ef",
    category: NeuronCategory = NeuronCategory.Sensor(
        subCategory = NeuronCategory.SensorSubcategory.Entity
    )
) : NumericalInputNeuron(value, id, category) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int): Double {
        value = when (entity.direction) {
            Direction.North -> (entity.fieldOfView.getFront()
                .find { it?.hasEntity == true }?.coordinates?.y)?.let { (it - entity.coordinates.y) * 0.25 } ?: -1.0

            Direction.East -> (entity.fieldOfView.getFront()
                .find { it?.hasEntity == true }?.coordinates?.x)?.let { worldSize - it * 0.25 } ?: -1.0

            Direction.South -> (entity.fieldOfView.getFront()
                .find { it?.hasEntity == true }?.coordinates?.y)?.let { 4 - it * 0.25 } ?: -1.0

            Direction.West -> (entity.fieldOfView.getFront()
                .find { it?.hasEntity == true }?.coordinates?.x)?.let { 4 - it * 0.25 } ?: -1.0
        }
        return value
    }
}