package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StartingMoneyAssignerTest {

    private val assigner = StartingMoneyAssigner()

    @Test
    fun `assign should give exactly 1_000_000 to a single player`() {
        val player = Player(name = "Player1")
        assigner.assign(player)
        assertEquals(1_000_000, player.balance)
    }

    @Test
    fun `assignToAll should assign 1_000_000 to each player`() {
        val players = listOf(
            Player(name = "Player1"),
            Player(name = "Player2"),
            Player(name = "Player3"),
            Player(name = "Player4")
        )
        assigner.assignToAll(players)
        for (player in players) {
            assertEquals(1_000_000, player.balance, "Player ${player.name} did not receive the correct amount")
        }
    }

    @Test
    fun `assign should not overwrite existing player balance`() {
        val player = Player(name = "Player1", balance = 500_000)
        assigner.assign(player)
        assertEquals(500_000, player.balance, "Player balance should not be overwritten")
    }

    @Test
    fun `assign should store correct money denominations in player state`() {
        val player = Player(name = "Player1")
        assigner.assign(player)

        val expectedMoney = mapOf(
            5_000 to 10,
            10_000 to 5,
            50_000 to 4,
            100_000 to 7
        )

        assertEquals(expectedMoney, player.money, "Player ${player.name} does not have the correct denominations")
    }

    @Test
    fun `assign called multiple times should not duplicate money`() {
        val player = Player(name = "Player1")
        assigner.assign(player)
        assigner.assign(player) // Second call

        assertEquals(1_000_000, player.balance)
        assertEquals(4, player.money.size) // Check denominations unchanged
    }
}