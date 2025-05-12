/**
 * @file DiceStrategy.kt
 * @author eles17
 * @since
 * @description Interface für austauschbare Würfelstrategien (Strategy Pattern).
 */
package at.mankomania.server.util

/**
 * Functional Interface for interchangeable dice rolling strategies.
 * The strategy defines how the dice are rolled and returns the result containing both dice and their sum.
 */
// Defines a contract, what a dice strategy must do, without saying how
fun interface DiceStrategy {
    /**
     * Rolls the dice and returns the result.
     * @return The result of the dice roll containing both dice and their sum.
     */
    fun roll(): DiceResult
}