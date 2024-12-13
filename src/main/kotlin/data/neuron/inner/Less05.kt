package data.neuron.inner

import data.entity.Entity
import data.neuron.ActivationGroup
import data.neuron.InnerCategory
import data.neuron.InnerNeuron
import data.neuron.InputNeuron

class Less05 (
    id: String = "<0.5",
    category: InnerCategory = InnerCategory.Logical,
    override var value: Float = 0.0f,
    override val sources: Array<InputNeuron> = arrayOf()
) : InnerNeuron(id, category, value, sources, activationGroup = ActivationGroup.AG19) {
    override fun evaluate(entity: Entity, worldSize: Int) {

    }

    override fun getExcitementValue(): Float {
        return 0.0f
    }
}