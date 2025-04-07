package at.mankomania.server.service
import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class BetModelTest {

    @Test
    fun testBetModel() {
        val bet = Bet(playerId = "id123", horseColor = HorseColor.RED, amount = 500)

        assertEquals( "id123", bet.playerId)
        assertEquals( HorseColor.RED, bet.horseColor)
        assertEquals( 500, bet.amount)
    }
}
