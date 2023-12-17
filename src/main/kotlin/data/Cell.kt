package data

import data.entity.Direction

class Cell(
    val id: Int,
    val coordinates: Coordinates,
    var hasEntity: Boolean,
    var hasFood: Boolean,
    // val type: CellType
)

data class Coordinates(
    val x: Int,
    val y: Int
) {
    // Get geographical coordinates
    private fun getNorth(step: Int) =
        if (y > step - 1) {
            copy(y = y - step)
        } else null

    private fun getNorthEast(step: Int, worldSize: Int) =
        if (x < worldSize - step && y > step - 1) {
            copy(x = x + step, y = y - step)
        } else null

    private fun getNorthWest(step: Int) =
        if (x > step - 1 && y > step - 1) {
            copy(x = x - step, y = y - step)
        } else null

    private fun getEast(step: Int, worldSize: Int) =
        if (x < worldSize - step) {
            copy(x = x + step)
        } else null

    private fun getSouth(step: Int, worldSize: Int) =
        if (y < worldSize - step) {
            copy(y = y + step)
        } else null

    private fun getSouthEast(step: Int, worldSize: Int) =
        if (x < worldSize - step && y < worldSize - step) {
            copy(x = x + step, y = y + step)
        } else null

    private fun getSouthWest(step: Int, worldSize: Int) =
        if (x > step - 1 && y < worldSize - step) {
            copy(x = x - step, y = y + step)
        } else null

    private fun getWest(step: Int) =
        if (x > step - 1) {
            copy(x = x - step)
        } else null

    // Get relative coordinates
    fun getFrontCoord(step: Int, direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getNorth(step)
            Direction.East -> getEast(step, worldSize)
            Direction.South -> getSouth(step, worldSize)
            Direction.West -> getWest(step)
        }


    fun getFrontRightCoord(step: Int, direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getNorthEast(step, worldSize)
            Direction.East -> getSouthEast(step, worldSize)
            Direction.South -> getSouthWest(step, worldSize)
            Direction.West -> getNorthWest(step)
        }

    fun getRightCoord(step: Int, direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getEast(step, worldSize)
            Direction.East -> getSouth(step, worldSize)
            Direction.South -> getWest(step)
            Direction.West -> getNorth(step)
        }

    fun getBackRightCoord(direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getSouthEast(1, worldSize)
            Direction.East -> getSouthWest(1, worldSize)
            Direction.South -> getNorthWest(1)
            Direction.West -> getNorthEast(1, worldSize)
        }

    fun getBackCoord(direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getSouth(1, worldSize)
            Direction.East -> getWest(1)
            Direction.South -> getNorth(1)
            Direction.West -> getEast(1, worldSize)
        }

    fun getBackLeftCoord(direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getSouthWest(1, worldSize)
            Direction.East -> getNorthWest(1)
            Direction.South -> getNorthEast(1, worldSize)
            Direction.West -> getSouthEast(1, worldSize)
        }

    fun getLeftCoord(step: Int, direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getWest(step)
            Direction.East -> getNorth(step)
            Direction.South -> getEast(step, worldSize)
            Direction.West -> getSouth(step, worldSize)
        }

    fun getFrontLeftCoord(step: Int, direction: Direction, worldSize: Int) =
        when (direction) {
            Direction.North -> getNorthWest(step)
            Direction.East -> getNorthEast(step, worldSize)
            Direction.South -> getSouthEast(step, worldSize)
            Direction.West -> getSouthWest(step, worldSize)
        }
}

/* TODO: Remove if not used
// In the future we want to implement landscape that the entities will interact with, like swimming in water and
 climbing on rocks
enum class CellType {
    Land, Water, Rock
}*/
