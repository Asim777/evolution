package data.neuron

import data.Coordinates
import data.entity.Direction

interface NeuronCategory

sealed class SensorCategory : NeuronCategory {
    object EndOfWorld : SensorCategory() {
        fun isEndOfWorld(
            relativeDirection: RelativeDirection,
            entityDirection: Direction,
            coordinates: Coordinates,
            worldSize: Int
        ) = when (entityDirection) {
            Direction.North ->
                when (relativeDirection) {
                    RelativeDirection.Front -> isEndOfWorldNorth(coordinates)
                    RelativeDirection.Behind -> worldSize - coordinates.y < 3
                    RelativeDirection.Left -> isEndOfWorldWest(coordinates)
                    RelativeDirection.Right -> isEndOfWorldEast(coordinates, worldSize)
                }

            Direction.East -> {
                when (relativeDirection) {
                    RelativeDirection.Front -> isEndOfWorldEast(coordinates, worldSize)
                    RelativeDirection.Behind -> worldSize - coordinates.x < 3
                    RelativeDirection.Left -> isEndOfWorldNorth(coordinates)
                    RelativeDirection.Right -> isEndOfWorldSouth(coordinates, worldSize)
                }
            }

            Direction.South -> {
                when (relativeDirection) {
                    RelativeDirection.Front -> isEndOfWorldSouth(coordinates, worldSize)
                    RelativeDirection.Behind -> coordinates.y < 2
                    RelativeDirection.Left -> isEndOfWorldEast(coordinates, worldSize)
                    RelativeDirection.Right -> isEndOfWorldWest(coordinates)
                }
            }

            Direction.West -> {
                when (relativeDirection) {
                    RelativeDirection.Front -> isEndOfWorldWest(coordinates)
                    RelativeDirection.Behind -> coordinates.x < 2
                    RelativeDirection.Left -> isEndOfWorldSouth(coordinates, worldSize)
                    RelativeDirection.Right -> isEndOfWorldNorth(coordinates)
                }
            }
        }

        fun getDistanceToEndOfWorld(entity: data.entity.Entity, worldSize: Int) : Float = when (entity.direction) {
            Direction.North -> entity.coordinates.y * 0.25f
            Direction.East -> (worldSize - entity.coordinates.x - 1) * 0.25f
            Direction.South -> (worldSize - entity.coordinates.y - 1) * 0.25f
            Direction.West -> entity.coordinates.x * 0.25f
        }

        fun getDistanceToEndOfWorldBehind(entity: data.entity.Entity, worldSize: Int) : Float = when (entity.direction) {
            Direction.North -> (worldSize - entity.coordinates.y - 1) * 0.25f
            Direction.East -> (worldSize - entity.coordinates.x - 1) * 0.25f
            Direction.South -> entity.coordinates.y * 0.25f
            Direction.West -> entity.coordinates.x * 0.25f
        }

        private fun isEndOfWorldNorth(coordinates: Coordinates) = coordinates.y - 5 < 0
        private fun isEndOfWorldSouth(coordinates: Coordinates, worldSize: Int) = coordinates.y + 4 < worldSize
        private fun isEndOfWorldEast(coordinates: Coordinates, worldSize: Int) = coordinates.x + 4 < worldSize
        private fun isEndOfWorldWest(coordinates: Coordinates) = coordinates.x - 5 < 0
    }

    object Entity : SensorCategory() {

    }
    object Food : SensorCategory()
    object EntityDensity : SensorCategory ()
    object Distance : SensorCategory ()
    object GeneticSimilarity : SensorCategory ()
}

enum class InnerCategory : NeuronCategory {
    Logical, Numerical
}

enum class SinkCategory : NeuronCategory {
    Movement, Turn, Eat, Mate
}

enum class RelativeDirection {
    Front, Behind, Left, Right
}