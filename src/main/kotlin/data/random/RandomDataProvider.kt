package data.random

interface RandomDataProvider {

    /**
     * Generate a random number in range of [0, max]
     *
     * @param max positive integer to apply as an exclusive upper limit to the range of random numbers
     * @return generated Int value
     */
    fun getRandomInteger(max: Int): Int

    /**
     * Generate a random float number in range of [0.0, 1.0]
     *
     * @param decimalPoints number of decimal points that the generated float number should have
     * @return generated Float value
     */
    fun getRandomFloat(decimalPoints: Int): Float

    //TODO: Delete if not needed
    fun reset()
}