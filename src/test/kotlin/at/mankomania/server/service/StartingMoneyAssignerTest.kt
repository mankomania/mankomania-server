package at.mankomania.server.service

import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class StartingMoneyAssignerTest {

    private lateinit var assigner: StartingMoneyAssigner
    private lateinit var socketService: PlayerSocketService

    @BeforeEach
    fun setUp() {
        socketService = mock(PlayerSocketService::class.java)
        assigner = StartingMoneyAssigner(socketService)
    }
    @Test
    fun `assign should give exactly 1_000_000 to a single player`() {
        val player = Player(name = "Player1")

        assigner.assign(player)

        assertEquals(1_000_000, player.balance)
        verify(socketService).sendFinancialState(player)
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
            verify(socketService).sendFinancialState(player)
        }

        verifyNoMoreInteractions(socketService)
    }

    @Test
    fun `assign should not overwrite existing player balance`() {
        val player = Player(name = "Player1", balance = 500_000)

        assigner.assign(player)

        assertEquals(500_000, player.balance, "Player balance should not be overwritten")
        verify(socketService, never()).sendFinancialState(player)
    }
}