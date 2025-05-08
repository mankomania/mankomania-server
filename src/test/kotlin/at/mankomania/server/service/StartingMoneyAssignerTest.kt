package at.mankomania.server.service

import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class StartingMoneyAssignerWithSocketTest {

    private lateinit var socketService: PlayerSocketService
    private lateinit var assigner: StartingMoneyAssigner

    @BeforeEach
    fun setUp() {
        socketService = mock(PlayerSocketService::class.java)
        assigner = StartingMoneyAssigner(socketService)
    }

    @Test
    fun `assignToAll should notify only players who had no money`() {
        val player1 = Player(name = "Player1", balance = 0, money = null)
        val player2 = Player(name = "Player2", balance = 100_000, money = mutableMapOf(100_000 to 1))
        val player3 = Player(name = "Player3", balance = 0, money = null)

        val players = listOf(player1, player2, player3)

        assigner.assignToAll(players)

        assertEquals(550_000, player1.balance)
        assertEquals(100_000, player2.balance)
        assertEquals(550_000, player3.balance)

        verify(socketService).sendFinancialState(player1)
        verify(socketService, never()).sendFinancialState(player2)
        verify(socketService).sendFinancialState(player3)

        verifyNoMoreInteractions(socketService)
    }
}