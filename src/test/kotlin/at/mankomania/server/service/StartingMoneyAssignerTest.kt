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
}
