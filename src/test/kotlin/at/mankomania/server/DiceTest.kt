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
        val dice = Dice(DefaultDiceStrategy()) // HAS PARAMETER NOW CHECK :...
        repeat(100){
            val result = dice.roll()
            assertTrue("Dice roll should be between 2 and 12, got $result", result in 2..12)
        }
    }
}