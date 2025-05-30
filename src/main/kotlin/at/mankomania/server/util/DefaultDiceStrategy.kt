package at.mankomania.server.util

import kotlin.random.Random

/**
 * Default dice roll strategy using Random.nextInt() to simulate rolling two dice.
 */
class DefaultDiceStrategy : DiceStrategy {
    override fun roll(): DiceResult {
        val die1 = Random.nextInt(1, 7) // Rolls between 1 and 6
        val die2 = Random.nextInt(1, 7) // Rolls between 1 and 6
        // concrete implementation, this is a normal dice
        return DiceResult(die1, die2) //return separate values for each die
    }
}