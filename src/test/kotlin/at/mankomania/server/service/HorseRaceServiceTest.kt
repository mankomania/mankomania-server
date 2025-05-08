package at.mankomania.server.service

import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import at.mankomania.server.model.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class HorseRaceServiceTest {

    private val horseRaceService = HorseRaceService()

    @Test
    fun `test spin roulette should return a valid HorseColor`() {
        val color = horseRaceService.spinRoulette()
        assertTrue(HorseColor.values().contains(color), "Spin roulette should return a valid HorseColor")
    }
    @Test
    fun `test calculateWinnings should return correct payouts`() {
        val bets = listOf(
            Bet(playerId = "player1", horseColor = HorseColor.RED, amount = 100),
            Bet(playerId = "player2", horseColor = HorseColor.BLUE, amount = 200),
            Bet(playerId = "player3", horseColor = HorseColor.RED, amount = 50)
        )
        val winningColor = HorseColor.RED
        val winnings = horseRaceService.calculateWinnings(bets, winningColor)

        assertEquals(200, winnings["player1"], "Player1 should win with the correct payout")
        assertEquals(0, winnings["player2"], "Player2 should lose and get 0")
        assertEquals(100, winnings["player3"], "Player3 should win with the correct payout")
    }


    @Test
    fun `test place bet - player does not exist should return false`() {
        val result = horseRaceService.placeBet("non-existent-player", HorseColor.RED, 500)
        assertFalse(result, "Placing a bet for a non-existent player should return false")
    }

    @Test
    fun `test place bet - not enough balance should return false`() {
        val player = Player(name = "Player1", balance = 100)
        horseRaceService.registerPlayer(player)

        val result = horseRaceService.placeBet("Player1", HorseColor.RED, 200)
        assertFalse(result, "Placing a bet with insufficient balance should return false")
    }

    @Test
    fun `test place bet - valid bet should return true`() {
        val player = Player(name = "Player1", balance = 1000)
        horseRaceService.registerPlayer(player)

        val result = horseRaceService.placeBet("Player1", HorseColor.RED, 500)
        assertTrue(result, "Placing a valid bet should return true")
        assertEquals(500, player.balance, "Player's balance should be reduced by the bet amount")
    }
}



