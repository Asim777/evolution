package data.neuron.sensor.food

import data.entity.Entity
import data.entity.getImmediateFront
import data.neuron.SensorCategory
import data.neuron.SensorNeuron

class FoodImmediateFront(
    id: String = "Fif",
    category: SensorCategory = SensorCategory.Food,
    override var value: Float = 0.0f
) : SensorNeuron(id, category, value) {
    // TODO: Write tests for method
    override fun evaluate(entity: Entity, worldSize: Int) {
        // Base value is calculated as 1 / distance. Because the cell immediately in front will always have a distance
        // 0.25, and there can be only one Food in that cell, the base value is 4
        val baseValue = if (entity.fieldOfView.getImmediateFront()?.hasFood == true) 4.0f else 0.0f

        // TODO: Revise these coefficients in future when the model gets more complex. Entities might want to choose
        // between moving in direction of food or other entities for reasons other than hunger and mating and
        // hard-coding this dependency can restrain the emergence of these types of behavior
        val hungerCurrentLevelCoefficient = 1 + (100 - entity.hunger) / 100
        value = baseValue * hungerCurrentLevelCoefficient/* * entity.geneticHungerCoefficient */
    }
}
