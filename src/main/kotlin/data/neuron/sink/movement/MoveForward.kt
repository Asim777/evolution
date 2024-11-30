package data.neuron.sink.movement

import data.neuron.*

class MoveForward(
    id: String = "Mf",
    category: SinkCategory = SinkCategory.Movement,
    override val sources: Array<InputNeuron> = arrayOf()
) : SinkNeuron(id, category, sources) {
    override fun getExcitementValue(): Float {
        var excitementValues = Array(sources.size) { 0.0f }
                 sources.forEachIndexed { index, input ->
            when (input.type) {
                NeuronType.Logical -> {
                   when (input) {
                       is SensorNeuron -> {
                           excitementValues[index] = if (input.value is Boolean && input.value == true) 1.0f else 0.0f
                       }
                       is InnerNeuron -> {
                           excitementValues[index] = if (input.value is Boolean && input.value == true) 1.0f else 0.0f
                       }
                   }
                }

                NeuronType.Numerical -> {
                    when (input) {
                        is SensorNeuron -> {}
                        is InnerNeuron -> {}
                    }
                }

                else -> return false
            }
        }
    }
}
