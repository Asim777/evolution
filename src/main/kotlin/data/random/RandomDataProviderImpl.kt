package data.random
import kotlin.math.pow

/**
 * Returns a random coordinate in the World that is not currently occupied by any entity and doesn't have a food
 * in it
 *
 * @return Coordinates - random non-occupied coordinate without food
 */
class RandomDataProviderImpl: RandomDataProvider {

    private var randomBitsString: String? = null
    private var randomNumberCursorPosition = 0
    private var currentRandomBitsFileNumber = 0

    init {
         randomBitsString = object {}.javaClass.getResource("/random/0")?.readText()
    }

    override fun getRandomInteger(max: Int): Int {
        // Calculate a minimum number of bits needed to represent the maximum
        val minNumberOfBits = Integer.toBinaryString(max).length

        goToNextRandomNumberFileIfNecessary(minNumberOfBits)
        // Get minNumberOfBits bits from a file starting at randomNumberCursorPosition
        return getRandomNumber(minNumberOfBits, max)
    }

    /**
     * Generate a random float number in range of [0.0, 1.0]
     *
     * @param decimalPoints number of decimal points that the generated float number should have
     * @return generated Float value
     */
    override fun getRandomFloat(decimalPoints: Int): Float {
        val max = 10.0.pow(decimalPoints).toInt()
        // Calculate a minimum number of bits needed to represent the float number with given decimal points
        return getRandomInteger(max).toFloat() / 100
    }

    override fun reset() {
        randomNumberCursorPosition = 0
        currentRandomBitsFileNumber = 0
    }

    private fun goToNextRandomNumberFileIfNecessary(minNumberOfBits: Int) {
        val endIndex = randomNumberCursorPosition + minNumberOfBits
        randomBitsString?.let {
            if (endIndex >= it.length) {
                this.randomBitsString =
                    object {}.javaClass.getResource("/random/${++currentRandomBitsFileNumber}")?.readText() ?: ""
                randomNumberCursorPosition = 0
            }
        } ?: throw IllegalStateException("Failed to generate random number. randomBitsString is null")
    }

    private fun getRandomNumber(minNumberOfBits: Int, max: Int): Int {
        val randomBits = runCatching {
            randomBitsString?.substring(
                randomNumberCursorPosition, randomNumberCursorPosition + minNumberOfBits
            ) ?: throw IllegalStateException("Failed to generate random number. randomBits is null")
        }.getOrElse {
            throw IllegalStateException("Ran out of random bits in a file: @${currentRandomBitsFileNumber}")
        }
        // Update cursor position so that the next time this method is called, it fetches unused bits from the file
        randomNumberCursorPosition += minNumberOfBits
        // Convert selected random bits to an integer and return it
        runCatching {
            var randomNumber = randomBits.toInt(2)
            // Random number picked from minNumberOfBits can be bigger than our maximum. We need to scale it down
            // For that, we check if randomNumber is bigger or equal to max, and if yes
            // we calculate the maximum number that can be represented with minNumberOfBits, and we multiply randomNumber by
            // the quotient of max to maxNumberWithMinBits
            if (randomNumber >= max) {
                val maxNumberWithMinBits = (2.0.pow(minNumberOfBits) - 1).toInt()
                randomNumber = (randomNumber * max.toFloat() / maxNumberWithMinBits).toInt() - 1
            }
            return randomNumber
        }.getOrElse {
            throw IllegalStateException("Failed to convert bits string into integer. Random number: $randomBits")
        }
    }

    //TODO: Delete if not needed in Simulation steps
    /*fun getPseudoRandomNonOccupiedCoordinateWithoutFood(
        worldSize: Int,
        world: HashMap<Int, HashMap<Int, Cell>>
    ): Coordinates {
        var coord: Coordinates
        var cell: Cell?
        do {
            coord = Coordinates(
                x = getRandomInteger(worldSize), y = getRandomInteger(worldSize)
            )
            cell = world[coord.x]?.get(coord.y)
        } while (cell?.hasEntity == true || cell?.hasFood == true)
        return coord
    }*/
}