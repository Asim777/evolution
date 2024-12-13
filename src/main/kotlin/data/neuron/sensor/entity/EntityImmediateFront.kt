package data.neuron.sensor.entity

import data.entity.Entity
import data.entity.getImmediateFront
import data.neuron.ActivationGroup
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class EntityImmediateFront(
    id: String = "Eif",
    category: SensorCategory = SensorCategory.Entity,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value, ActivationGroup.AG9) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        // Base value is calculated as 1 / distance. Because the cell immediately in front will always have a distance
        // 0.25, and there can be only one Entity in that cell, the base value is 4
        val baseValue = if (entity.fieldOfView.getImmediateFront()?.hasEntity == true) 4.0f else 0.0f

        // TODO: Revise these coefficients in future when the model gets more complex. Entities might want to choose
        // between moving in direction of food or other entities for reasons other than hunger and mating and
        // hard-coding this dependency can restrain the emergence of these types of behavior
        val matingDriveCurrentLevelCoefficient = 1 + (100 - entity.matingDrive) / 100
        value = baseValue * matingDriveCurrentLevelCoefficient/* * entity.geneticMatingDriveCoefficient*/
    }
}
