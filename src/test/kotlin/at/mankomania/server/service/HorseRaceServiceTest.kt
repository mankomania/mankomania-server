package at.mankomania.server.service

import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import org.junit.jupiter.api.Assertions.*
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

    @Test
    fun `test spinRoulette returns a non-null HorseColor`() {
        val result = horseRaceService.spinRoulette()
        assertNotNull(result)
    }

    @Test
    fun `test registerPlayer stores player correctly`() {
        val balance = 2025
        horseRaceService.registerPlayer(player = Player("test-player", balance))

        val player = horseRaceService.getPlayer("test-player")

        assertNotNull(player)
        assertEquals(balance, player!!.balance)
    }

    @Test
    fun `test placeBet fails if player does not exist`() {
        val result = horseRaceService.placeBet("ghost-player", HorseColor.BLUE, 2025)
        assertFalse(result)
    }

    @Test
    fun `test placeBet fails if balance is too low`() {
        horseRaceService.registerPlayer(Player("poor-player", 0))
        val result = horseRaceService.placeBet("poor-player", HorseColor.RED, 500)
        assertFalse(result)
    }

    @Test
    fun `test placeBet succeeds with valid balance`() {
        horseRaceService.registerPlayer(Player("rich-player", 1000))
        val result = horseRaceService.placeBet("rich-player", HorseColor.RED, 500)
        assertTrue(result)
    }
}
