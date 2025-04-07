package at.mankomania.server.service

import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor

import at.mankomania.server.service.HorseRaceService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HorseRaceServiceTest2 {

    @Test
    fun `calculateWinnings returns correct payouts`() {
        // Given
        val bets = listOf(
            Bet(playerId = "player1", horseColor = HorseColor.RED, amount = 100),
            Bet(playerId = "player2", horseColor = HorseColor.BLUE, amount = 200),
            Bet(playerId = "player3", horseColor = HorseColor.RED, amount = 50)
        )
        val winningColor = HorseColor.RED
        val horseRaceService = HorseRaceService() // Crea un'istanza di HorseRaceService

        // When
        val result = horseRaceService.calculateWinnings(bets, winningColor)

        // Then
        assertEquals(200, result["player1"])
        assertEquals(0, result["player2"])
        assertEquals(100, result["player3"])
    }
}