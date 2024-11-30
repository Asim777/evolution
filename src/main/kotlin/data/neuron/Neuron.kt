package data.neuron

import data.*
import data.entity.*
import data.neuron.sensor.entity.EntityImmediateFront
import data.neuron.sensor.end_of_world.EndOfWorldFront
import data.neuron.sensor.food.FoodImmediateFront
import data.neuron.sink.movement.MoveForward
import java.lang.IllegalArgumentException

/** Interface for all neurons
 *
 * @property id String : unique id of the Neuron, example "EoWf" for "End of World in Front"
 * @property category NeuronCategory : category of the neuron
 */
interface Neuron {
    val id: String
    val category: NeuronCategory
}

// region Input, Output Neurons
/**
 * Interface for all Input Neurons. Input Neurons are Neurons that provide some information to the Output Neurons
 *
 * @property value T : the value of the observed property. It can be boolean for Logical Input Neurons and integer
 * for Numerical Input Neurons
 */
interface InputNeuron : Neuron {
    // TODO: We probably don't need to save value as property. We get a newly calculated value every
    //  time evaluate is called
    val value: Float
    /*val type: NeuronType*/
    fun evaluate(entity: Entity, worldSize: Int): Float
}

/**
 * Interface for all Output Neurons. Output Neurons are [Neuron]s that receive some information from the [InnerNeuron]s
 *
 * @property sources Array<InputNeuron<Any> : the list of the sources that contribute information to this output neuron
 */
interface OutputNeuron : Neuron {
    val sources: Array<InputNeuron>

    fun getExcitementValue() : Float
}
// endregion

// region Sensor, Inner, Sink Neurons
/**
 * Base class for all Sensor Neurons. Sensor Neurons are [Neuron]s that observe some information from the World.
 * Sensor Neurons provide input to some Inner or [SinkNeuron]
 *
 *  @property value T : the value of the observed property. It can be boolean for Logical Input Neurons and integer
 *  for Numerical Input Neurons
 *  @property id String : unique id of the Neuron, example "EoWf" for "End of World in Front"
 *  @property category [SensorCategory] : category of the Sensor neuron
 */
abstract class SensorNeuron(
    override val id: String,
    override val category: SensorCategory,
    // TODO: We probably don't need to save value as property. We get a newly calculated value every
    //  time evaluate is called
    override val value: Float,
    /*override val type: NeuronType*/
) : InputNeuron

/**
 * Base class for all Inner Neurons. Inner Neurons are [Neuron]s that are associated with some logical or mathematical
 * operation and serve as part of the rule that connects [SensorNeuron]s and [SinkNeuron]s.
 * Input Inner Neurons provide input for some [SinkNeuron]. Output Inner Neurons receive input from some [SensorNeuron].
 *
 *  @property value T : the value of the observed property. It can be boolean for Logical Input Neurons and integer
 *  for Numerical Input Neurons
 *  @property id String : unique id of the Neuron, example "EoWf" for "End of World in Front"
 *  @property category SensorCategory : category of the Inner Neuron
 */
abstract class InnerNeuron(
    override val id: String,
    override val category: InnerCategory,
    override val value: Float,
    override val sources: Array<InputNeuron>,
    /*override val type: NeuronType*/
) : InputNeuron, OutputNeuron

/**
 * Base class for all Sink Neurons. Sink Neurons are [Neuron]s that execute some action
 * Sink Neurons receive input from some Sensor or [InnerNeuron]
 *
 *  @property sources Array<InputNeuron<Any> : the list of the sources that contribute information to this output neuron
 *  @property id String : unique id of the Neuron, example "EoWf" for "End of World in Front"
 *  @property category SinkCategory : category of the Sink neuron
 */
abstract class SinkNeuron(
    override val id: String,
    override val category: SinkCategory,
    override val sources: Array<InputNeuron>
) : OutputNeuron
// endregion

//TODO: Rethink whether we need this categorization. When we evaluate excitement in Sink neurons,
// we convert true to 1.0f and false to 0.0f anyway
/*enum class NeuronType {
    Logical,
    Numerical
}*/

fun getNeurons(numberOfNeurons: Int): List<Neuron> =
    when (numberOfNeurons) {
        9 -> listOf(
            EndOfWorldFront(),
            EntityImmediateFront(),
            FoodImmediateFront(),
            MoveForward(),
            /*TurnRight(),
            TurnLeft(),
            Eat(),
            Mate()*/
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
                sensorNeurons = find { it.first == SensorCategory::class.java.canonicalName }?.second ?: 0,
                innerNeurons = find { it.first == InnerCategory::class.java.canonicalName }?.second ?: 0,
                sinkNeurons = find { it.first == SinkCategory::class.java.canonicalName }?.second ?: 0,
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