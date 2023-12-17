package data.entity

import androidx.compose.ui.graphics.Color
import data.Cell
import data.Coordinates
import data.Gene
import data.neuron.*
import java.lang.IllegalStateException

data class Entity(
    val id: Int,
    val genome: Array<Gene>,
    val color: Color,
    var coordinates: Coordinates,
    var direction: Direction,
    var fieldOfView: FieldOfView,
    var age: Int,
    var energy: Int,
    var hunger: Int,
    //var thirst: Int,
    var sexualDrive: Int,
    //var sleepiness: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity

        if (id != other.id) return false
        if (!genome.contentEquals(other.genome)) return false
        if (color != other.color) return false
        if (coordinates != other.coordinates) return false
        if (direction != other.direction) return false
        if (fieldOfView != other.fieldOfView) return false
        if (age != other.age) return false
        if (energy != other.energy) return false
        if (hunger != other.hunger) return false
        return sexualDrive == other.sexualDrive
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + genome.contentHashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + coordinates.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + fieldOfView.hashCode()
        result = 31 * result + age
        result = 31 * result + energy
        result = 31 * result + hunger
        result = 31 * result + sexualDrive
        return result
    }

    // Get relative coordinates
    // TODO: Write tests for methods in this block


    //TODO: Write JavaDoc
    // TODO: Write tests for method
    fun calculateFieldOfView(world: HashMap<Int, HashMap<Int, Cell>>) {
        val worldSize = world.size
        fun Coordinates.getCell() = world[this.x]?.get(this.y)

        with(fieldOfView) {
            sl = coordinates.getCell()
            f1 = coordinates.getFrontCoord(1, direction, worldSize)?.getCell()
            fr = coordinates.getFrontRightCoord(1, direction, worldSize)?.getCell()
            r = coordinates.getRightCoord(1, direction, worldSize)?.getCell()
            br = coordinates.getBackRightCoord(direction, worldSize)?.getCell()
            b = coordinates.getBackCoord(direction, worldSize)?.getCell()
            bl = coordinates.getBackLeftCoord(direction, worldSize)?.getCell()
            l = coordinates.getLeftCoord(1, direction, worldSize)?.getCell()
            fl = coordinates.getFrontLeftCoord(1, direction, worldSize)?.getCell()
            f2 = coordinates.getFrontCoord(2, direction, worldSize)?.getCell()
            f1r2 = f2?.coordinates?.getRightCoord(1, direction, worldSize)?.getCell()
            f1l2 = f2?.coordinates?.getLeftCoord(1, direction, worldSize)?.getCell()
            fr2 = coordinates.getFrontRightCoord(2, direction, worldSize)?.getCell()
            fl2 = coordinates.getFrontLeftCoord(2, direction, worldSize)?.getCell()
            f3 = coordinates.getFrontCoord(3, direction, worldSize)?.getCell()
            f2r3 = f3?.coordinates?.getRightCoord(1, direction, worldSize)?.getCell()
            f2l3 = f3?.coordinates?.getLeftCoord(1, direction, worldSize)?.getCell()
            f1r3 = f3?.coordinates?.getRightCoord(2, direction, worldSize)?.getCell()
            f1l3 = f3?.coordinates?.getLeftCoord(2, direction, worldSize)?.getCell()
            fr3 = coordinates.getFrontRightCoord(3, direction, worldSize)?.getCell()
            fl3 = coordinates.getFrontLeftCoord(3, direction, worldSize)?.getCell()
            f4 = coordinates.getFrontCoord(4, direction, worldSize)?.getCell()
            f3r4 = f3?.coordinates?.getRightCoord(1, direction, worldSize)?.getCell()
            f3l4 = f3?.coordinates?.getLeftCoord(1, direction, worldSize)?.getCell()
            f2r4 = f3?.coordinates?.getRightCoord(2, direction, worldSize)?.getCell()
            f2l4 = f3?.coordinates?.getLeftCoord(2, direction, worldSize)?.getCell()
            f1r4 = f3?.coordinates?.getRightCoord(3, direction, worldSize)?.getCell()
            f1l4 = f3?.coordinates?.getLeftCoord(3, direction, worldSize)?.getCell()
            fr4 = coordinates.getFrontRightCoord(4, direction, worldSize)?.getCell()
            fl4 = coordinates.getFrontLeftCoord(4, direction, worldSize)?.getCell()
        }
    }
}

fun Entity.evaluateInputData(worldSize: Int) {
    val outputNeuronsAndWeights: ArrayList<Entity.OutputCalculationData> = arrayListOf()

    genome.forEach { gene ->
        // Do not evaluate useless genes that can't possibly result in action
        when (gene.input.category) {
            is NeuronCategory.Sensor,
            is NeuronCategory.Inner -> {
                // TODO: Refactor to remove duplication
                if (gene.input is LogicalInputNeuron) {
                    gene.input.value = gene.input.evaluate(this@evaluateInputData, worldSize)
                }
                if (gene.input is NumericalInputNeuron) {
                    gene.input.value = gene.input.evaluate(this@evaluateInputData, worldSize)
                }
            }

            else -> {
                throw IllegalStateException("Sink neuron is assigned to Gene input")
            }
        }
    }
    // Find the [OutputNeuron] with the highest weight to perform that [OutputNeuron]'s action
    genome.find { it.weight }
}

private fun calculateOutput(outputCalculationData: ArrayList<Entity.OutputCalculationData>) : OutputNeuron {
    return outputCalculationData[indexOfBiggestValue].outputNeuron
}

fun Entity.performAction() {
    val sinkWeights =
        genome.forEach { gene ->

    }
}