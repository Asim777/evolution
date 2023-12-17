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

sealed interface Neuron<T> {
    val id: String
    val category: NeuronCategory
}

sealed class NeuronCategory {
    data class Sensor(val subCategory: SensorSubcategory) : NeuronCategory()
    data class Inner(val subCategory: InnerSubcategory) : NeuronCategory()
    data class Sink(val subCategory: SinkSubCategory) : NeuronCategory()

    enum class SensorSubcategory {
        EndOfWorld, Entity, Food, EntityDensity, Distance, GeneticSimilarity
    }

    enum class InnerSubcategory {
        Logical, Numerical
    }

    enum class SinkSubCategory {
        Movement, Turn, Eat, Mate
    }
}

abstract class LogicalInputNeuron(
    open var value: Boolean,
    override val id: String,
    override val category: NeuronCategory,
) : InputNeuron<Boolean>

abstract class NumericalInputNeuron(
    open var value: Float,
    override val id: String,
    override val category: NeuronCategory,
) : InputNeuron<Float>

interface InputNeuron<T> : Neuron<T> {
    fun evaluate(entity: Entity, worldSize: Int): T
}

abstract class OutputNeuron(
    override val id: String,
    override val category: NeuronCategory
) : Neuron<Coordinates?>

abstract class ValuelessNeuron(
    override val id: String,
    override val category: NeuronCategory
) : Neuron<Unit>

fun getNeurons(numberOfNeurons: Int): List<Neuron<out Any?>> =
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
            Mate(),
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