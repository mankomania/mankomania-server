/**
 * @file Dice.kt
 * @author eles17
 * @since 7.4.2025
 * @description resposible for rolling dice, delegating the catual roll logic to a strategy
 */
package at.mankomania.server.util


class Dice(private val strategy: DiceStrategy) {
    /**
     * Rolls the dice using the provided strategy.
     *
     * @return The result of the dice roll according to the strategy.
     */
    fun roll(): DiceResult = strategy.roll()
}