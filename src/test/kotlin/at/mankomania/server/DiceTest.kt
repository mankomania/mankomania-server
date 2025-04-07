package at.mankomania.server

import at.mankomania.server.util.Dice
import at.mankomania.server.util.DefaultDiceStrategy
import org.springframework.test.util.AssertionErrors.assertTrue
import org.junit.jupiter.api.Test



/**
 * @author eles17
 * Unit test class for the [Dice] functionality.
 */
class DiceTest {

    /**
     * Validates that the valid value for Dice roll is returned
     */
    @Test
    fun testRollDiceReturnsValidValue(){
        val dice = Dice(DefaultDiceStrategy())
        repeat(100){
            val result = dice.roll()
            assertTrue("Die 1 should be between 1 and 6, got ${result.die1}", result.die1 in 1..6)
            assertTrue("Die 2 should be between 1 and 6, got ${result.die2}", result.die2 in 1..6)
            assertTrue("Sum should equal die 1 + die 2, got ${result.sum}", result.sum == result.die1 + result.die2)
            assertTrue("Sum should be between 2 and 12, got ${result.sum}", result.sum in 2..12)
        }
    }
}