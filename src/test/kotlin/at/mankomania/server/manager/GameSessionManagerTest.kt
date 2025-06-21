package at.mankomania.server.manager

import at.mankomania.server.controller.GameController
import at.mankomania.server.model.Player
import at.mankomania.server.service.BankService
import at.mankomania.server.service.NotificationService
import at.mankomania.server.service.StartingMoneyAssigner
import at.mankomania.server.websocket.PlayerSocketService
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class GameSessionManagerTest {

    private lateinit var notificationService: NotificationService
    private lateinit var sessionManager: GameSessionManager
    private val gameId = "testGame"

    @BeforeEach
    fun setUp() {
        val playerSocketService = mock(PlayerSocketService::class.java)
        val moneyAssigner = StartingMoneyAssigner(playerSocketService)
        val bankService = BankService()
        val notificationService = mock(NotificationService::class.java)

        sessionManager = GameSessionManager(
            moneyAssigner,
            bankService,
            notificationService,
            playerSocketService)
    }

    @Test
    fun testJoinGameAddsNewPlayersAndPreventsDuplicates() {
        assertTrue(sessionManager.joinGame(gameId, "Franz"))
        assertTrue(sessionManager.joinGame(gameId, "Kafka"))
        assertFalse(sessionManager.joinGame(gameId, "Franz"))

        val players = sessionManager.getPlayers(gameId)
        assertEquals(2, players.size)
        assertEquals(listOf("Franz", "Kafka"), players.map(Player::name))
    }

    @Test
    fun testJoinGameRejectsMoreThan4Players() {
        // add 4 distinct names
        listOf("A", "B", "C", "D").forEach { name ->
            assertTrue(sessionManager.joinGame(gameId, name))
        }
        // fifth player should be rejected
        assertFalse(sessionManager.joinGame(gameId, "E"))
        assertEquals(4, sessionManager.getPlayers(gameId).size)
    }

    @Test
    fun testStartSessionReturnsNullForNonexistentLobby() {
        // no players at all
        assertNull(sessionManager.startSession("noLobby", 40))
    }

    @Test
    fun testStartSessionReturnsNullWhenOnlyOnePlayer() {
        // join only one player
        sessionManager.joinGame(gameId, "Solo")
        assertNull(sessionManager.startSession(gameId, 40))
    }

    @Test
    fun testStartSessionAssignsCorrectStartPositions() {
        // join 3 players directly in this test
        listOf("P1", "P2", "P3", "P4").forEach { sessionManager.joinGame(gameId, it) }
        val positions = sessionManager.startSession(gameId, 40)
        assertNotNull(positions)
        // expect evenly spaced positions: [0, 12, 32, 20]
        assertEquals(listOf(0, 12, 32, 20), positions)
    }

    @Test
    fun testStartSessionCreatesGameController() {
        // join 3 players directly in this test
        listOf("P1", "P2", "P3").forEach { sessionManager.joinGame(gameId, it) }
        sessionManager.startSession(gameId, 40)

        // verify controller creation
        val controller = sessionManager.getGameController(gameId)
        assertNotNull(controller)
        assertTrue(controller is GameController)

        // retrieve starting player and verify notification was sent
        val startingPlayer = sessionManager.getPlayers(gameId).firstOrNull { it.isTurn }
        if (startingPlayer != null) {
            val field = sessionManager
                .javaClass
                .getDeclaredField("notificationService")
                .apply { isAccessible = true }

            val realNotificationService = field.get(sessionManager) as NotificationService
            verify(realNotificationService, atLeastOnce()).sendPlayerStatus(startingPlayer)
        }
    }

    @Test
    fun testGetPlayersReturnsEmptyListForUnknownLobby() {
        assertTrue(sessionManager.getPlayers("unknown").isEmpty())
    }












}