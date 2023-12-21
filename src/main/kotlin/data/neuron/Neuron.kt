package data.neuron

import data.*
import data.entity.*
import data.neuron.entity.*
import data.neuron.sensor.end_of_world.EndOfWorldFront
import data.neuron.sensor.food.FoodImmediateFront
import data.neuron.sensor.food.FoodSameLocation
import data.neuron.sink.eat.Eat
import data.neuron.sink.mate.Mate
import data.neuron.sink.movement.MoveForward
import data.neuron.sink.turn.TurnLeft
import data.neuron.sink.turn.TurnRight
import java.lang.IllegalArgumentException

sealed interface Neuron {
    val id: String
    val category: NeuronCategory
}

abstract class InputNeuron<T> (
   open val value: T
) : Neuron {
    abstract fun evaluate(entity: Entity, worldSize: Int) : T
}

abstract class OutputNeuron(
    open val sources: Array<InputNeuron<Any>>
) : Neuron

abstract class SensorNeuron<T> (
    override val value: T,
    open val sensorCategory: NeuronCategory.SensorCategory
) : InputNeuron<T>(value)

abstract class InputInnerNeuron<T> (
    override val value: T,
    open val innerCategory: NeuronCategory.InnerCategory
) : InputNeuron<T>(value)

abstract class OutputInnerNeuron<T> (
    override val sources: Array<InputNeuron<Any>>,
    open val innerCategory: NeuronCategory.InnerCategory
) : OutputNeuron(sources)

abstract class SinkNeuron (
    override val sources: Array<InputNeuron<Any>>,
    val sinkCategory: NeuronCategory.SinkCategory
) : OutputNeuron(sources)

class LogicalSensorNeuron(
    override var value: Boolean,
    override val id: String,
    override val sensorCategory: NeuronCategory.SensorCategory
) : SensorNeuron<Boolean>(value, sensorCategory) {
    override fun evaluate(entity: Entity, worldSize: Int): Boolean {
        TODO("Not yet implemented")
    }

    override val category: NeuronCategory
        get() = TODO("Not yet implemented")

}

abstract class NumericalSensorNeuron(
    override var value: Float,
    override val id: String,
    override val sensorCategory: NeuronCategory.SensorCategory
) : SensorNeuron<Float>(value, sensorCategory)

sealed class NeuronCategory {
    enum class SensorCategory {
        EndOfWorld, Entity, Food, EntityDensity, Distance, GeneticSimilarity
    }

    enum class InnerCategory {
        Logical, Numerical
    }

    enum class SinkCategory {
        Movement, Turn, Eat, Mate
    }
}

fun getNeurons(numberOfNeurons: Int): List<Neuron> =
    when (numberOfNeurons) {
        9 -> listOf(
            EndOfWorldFront(),
            EntityImmediateFront(),
            FoodImmediateFront(),
            FoodSameLocation(),
            MoveForward(),
            TurnRight(),
            TurnLeft(),
            Eat(),
            Mate()
        )

        19 -> getNeurons(9) + listOf(
            /* DistanceObject(),
             EntityImmediateLeft(),
             EntityImmediateRight(),
             FoodImmediateLeft(),
             FoodImmediateRight(),
             EntityDensityAhead(),
             And(),
             Or(),
             Less05(),
             More05()*/
        )

        28 -> getNeurons(19) + listOf(
            /*DistanceFood(),
            FoodAhead(),
            EntityFront(),
            FoodFront(),
            FoodNeighborhood(),
            EntityDensityFront(),
            GeneticSimilarityAhead(),
            Not(),
            MoveRandomly()*/
        )

        35 -> getNeurons(28) + listOf(
            /*EntityDensityNeighborhood(),
            GeneticSimilarityFront(),
            Xor(),
            More025(),
            More075(),
            MoveBack(),
            TurnBack()*/
        )

        43 -> getNeurons(35) + listOf(
            /*DistanceEndOfWorld(),
            DistanceEntity(),
            FoodLeft(),
            FoodRight(),
            FoodBehind(),
            GeneticSimilarityNeighborhood(),
            Less025(),
            Less075()*/
        )

        50 -> getNeurons(43) + listOf(
            /*EntityLeft(),
            EntityRight(),
            EntityBehind(),
            More09(),
            Less01(),
            MoveRight(),
            MoveLeft()*/
        )

        57 -> getNeurons(50) + listOf(
            /*EndOfWorldLeft(),
            EndOfWorldRight(),
            EndOfWorldBehind(),
            GeneticSimilarityLeft(),
            GeneticSimilarityRight(),
            GeneticSimilarityBehind(),
            EntityNeighborhood*/
        )

        else -> {
            throw IllegalArgumentException("Wrong number of neurons inputed")
        }
    }

fun getNeuronDistributionByCategory(numberOfNeurons: Int): NumberOfNeurons =
    getNeurons(numberOfNeurons)
        .groupBy { it.category.javaClass }
        .map { it.key.canonicalName to it.value.size }
        .run {
            NumberOfNeurons(
                total = numberOfNeurons,
                sensorNeurons = find { it.first == NeuronCategory.Sensor::class.java.canonicalName }?.second ?: 0,
                innerNeurons = find { it.first == NeuronCategory.Inner::class.java.canonicalName }?.second ?: 0,
                sinkNeurons = find { it.first == NeuronCategory.Sink::class.java.canonicalName }?.second ?: 0,
            )
        }

fun getClippedNumberOfNeurons(numberOfNeurons: Int) = when (numberOfNeurons) {
    in 0..9 -> {
        getNeuronDistributionByCategory(9)
    }

    in 10..19 -> {
        getNeuronDistributionByCategory(19)
    }

    in 20..28 -> {
        getNeuronDistributionByCategory(28)
    }

    in 29..35 -> {
        getNeuronDistributionByCategory(35)
    }

    in 36..43 -> {
        getNeuronDistributionByCategory(43)
    }

    in 44..50 -> {
        getNeuronDistributionByCategory(50)
    }

    in 51..57 -> {
        getNeuronDistributionByCategory(57)
    }

    else -> {
        throw IllegalArgumentException("Wrong number of neurons inputed")
    }
}