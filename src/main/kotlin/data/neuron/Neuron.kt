package data.neuron

import data.*
import data.entity.*
import data.neuron.inner.*
import data.neuron.sensor.distance.DistanceEndOfWorld
import data.neuron.sensor.distance.DistanceEntity
import data.neuron.sensor.distance.DistanceFood
import data.neuron.sensor.distance.DistanceObject
import data.neuron.sensor.end_of_world.EndOfWorldBehind
import data.neuron.sensor.end_of_world.EndOfWorldFront
import data.neuron.sensor.end_of_world.EndOfWorldLeft
import data.neuron.sensor.end_of_world.EndOfWorldRight
import data.neuron.sensor.entity.*
import data.neuron.sensor.entity_density.EntityDensityAhead
import data.neuron.sensor.entity_density.EntityDensityFront
import data.neuron.sensor.entity_density.EntityDensityNeighborhood
import data.neuron.sensor.food.*
import data.neuron.sensor.genetic_similarity.*
import data.neuron.sink.eat.Eat
import data.neuron.sink.mate.Mate
import data.neuron.sink.movement.*
import data.neuron.sink.turn.TurnBack
import data.neuron.sink.turn.TurnLeft
import data.neuron.sink.turn.TurnRight
import java.lang.IllegalArgumentException

/** Interface for all neurons
 *
 * @property id String : unique id of the Neuron, example "EoWf" for "End of World in Front"
 * @property category NeuronCategory : category of the neuron
 */
interface Neuron {
    val id: String
    val category: NeuronCategory
    val activationGroup: ActivationGroup
}

// region Input, Output Neurons
/**
 * Interface for all Input Neurons. Input Neurons are Neurons that provide some information to the Output Neurons
 *
 * @property value T : the value of the observed property. It can be boolean for Logical Input Neurons and integer
 * for Numerical Input Neurons
 */
interface InputNeuron : Neuron {
    val value: Float

    /*val type: NeuronType*/
    fun evaluate(entity: Entity, worldSize: Int)
}

/**
 * Interface for all Output Neurons. Output Neurons are [Neuron]s that receive some information from the [InnerNeuron]s
 *
 * @property sources Array<InputNeuron<Any> : the list of the sources that contribute information to this output neuron
 */
interface OutputNeuron : Neuron {
    val sources: Array<InputNeuron>

    /**
     * Returns calculated excitementValue of the Output Neuron. If we have multiple Outputs of the same category,
     * with mutually exclusive actions, for instance, [MoveForward] and [MoveLeft], then the Neuron with the highest
     * excitementValue will get its action executed
     */
    fun getExcitementValue(): Float
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
    override val activationGroup: ActivationGroup
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
    override val activationGroup: ActivationGroup
    /*override val type: NeuronType*/
) : InputNeuron, OutputNeuron

/**
 * Base class for all Sink Neurons. Sink Neurons are [Neuron]s that execute some action.
 * They receive input from some [SensorNeuron] or [InnerNeuron]
 *
 *  @property sources Array : the array of the sources that contribute information to this output neuron
 *  @property id String : unique id of the Neuron, example "EoWf" for "End of World in Front"
 *  @property category SinkCategory : category of the Sink Neuron
 */
abstract class SinkNeuron(
    override val id: String,
    override val category: SinkCategory,
    override val sources: Array<InputNeuron>,
    override val activationGroup: ActivationGroup
) : OutputNeuron {
    override fun getExcitementValue(): Float = sources.map { it.value }.sum()
}
// endregion

//TODO: Rethink whether we need this categorization. When we evaluate excitement in Sink neurons,
// we convert true to 1.0f and false to 0.0f anyway
/*enum class NeuronType {
    Logical,
    Numerical
}*/

enum class ActivationGroup(value: Int) {
    AG9(9),
    AG19(19),
    AG28(28),
    AG35(35),
    AG43(43),
    AG50(50),
    AG57(57)
}

fun getNeurons(numberOfNeurons: Int = 0): List<Neuron> =
    when (numberOfNeurons) {
        0 -> getAllNeurons()
        9 -> getAllNeurons().filter { it.activationGroup == ActivationGroup.AG9 }
        19 -> getNeurons(9) + getAllNeurons().filter { it.activationGroup == ActivationGroup.AG19 }
        28 -> getNeurons(19) + getAllNeurons().filter { it.activationGroup == ActivationGroup.AG28 }
        35 -> getNeurons(28) + getAllNeurons().filter { it.activationGroup == ActivationGroup.AG35 }
        43 -> getNeurons(35) + getAllNeurons().filter { it.activationGroup == ActivationGroup.AG43 }
        50 -> getNeurons(43) + getAllNeurons().filter { it.activationGroup == ActivationGroup.AG50 }
        57 -> getNeurons(50) + getAllNeurons().filter { it.activationGroup == ActivationGroup.AG57 }
        else -> {
            throw IllegalArgumentException("Wrong number of neurons inputed")
        }
    }

fun getAllNeurons() =
    listOf(
        //9
        EndOfWorldFront(),
        EntityImmediateFront(),
        FoodImmediateFront(),
        MoveForward(),
        TurnRight(),
        TurnLeft(),
        Eat(),
        Mate(),

        //19
        DistanceObject(),
        EntityImmediateLeft(),
        EntityImmediateRight(),
        FoodImmediateLeft(),
        FoodImmediateRight(),
        EntityDensityAhead(),
        And(),
        Or(),
        Less05(),
        More05(),

        //28
        DistanceFood(),
        FoodAhead(),
        EntityFront(),
        FoodFront(),
        FoodNeighborhood(),
        EntityDensityFront(),
        GeneticSimilarityAhead(),
        Not(),
        MoveRandomly(),

        //35
        EntityDensityNeighborhood(),
        GeneticSimilarityFront(),
        Xor(),
        More025(),
        More075(),
        MoveBack(),
        TurnBack(),

        //43
        DistanceEndOfWorld(),
        DistanceEntity(),
        FoodLeft(),
        FoodRight(),
        FoodBehind(),
        GeneticSimilarityNeighborhood(),
        Less025(),
        Less075(),

        //50
        EntityLeft(),
        EntityRight(),
        EntityBehind(),
        More09(),
        Less01(),
        MoveRight(),
        MoveLeft(),

        //57
        EndOfWorldLeft(),
        EndOfWorldRight(),
        EndOfWorldBehind(),
        GeneticSimilarityLeft(),
        GeneticSimilarityRight(),
        GeneticSimilarityBehind(),
        EntityNeighborhood()
    )

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
        getNeuronDistributionByCategory(11)
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

/*
fun Neuron.toUiModel() = NeuronUiModel(
    name = this.id,
    explanation = this.,
    isActive = true,
    excitementValue = if (this is InputNeuron) this.value
)*/
