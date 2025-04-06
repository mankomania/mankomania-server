package at.mankomania.server.service

import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class HorseRaceServiceTest {

    private val horseRaceService = HorseRaceService()

    @Test
    fun `test runRace returns a HorseColor`() {
        val result = horseRaceService.runRace()
        assertNotNull(result)
    }

    @Test
    fun `test calculateWinnings returns correct payout`() {
        val bets = listOf(
            Bet("p1", HorseColor.RED, 100),
            Bet("p2", HorseColor.BLUE, 200)
        )

        val result = horseRaceService.calculateWinnings(bets, HorseColor.RED)

        assertEquals(200, result["p1"])
        assertEquals(0, result["p2"])
    }
}
