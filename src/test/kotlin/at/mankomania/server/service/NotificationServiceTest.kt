package at.mankomania.server.service

import at.mankomania.server.controller.dto.GameStateDto
import at.mankomania.server.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.mockito.Mockito.verify

/**
 * @file NotificationServiceTest.kt
 * @author Angela Drucks, Lev Starman
 * @since 2025-05-07
 * @description
 * Unit tests for NotificationService. Verifies correct WebSocket/STOMP broadcasting of:
 * - game state updates (GameStateDto)
 * - player movement and landing events
 * - full player status including balance, money breakdown, and turn info
 *
 * Ensures that all WebSocket destinations and payloads are correctly formatted and invoked.
 */

class NotificationServiceTest {

    private lateinit var messagingTemplate: SimpMessagingTemplate
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setup() {
        messagingTemplate = mock(SimpMessagingTemplate::class.java)
        notificationService = NotificationService(messagingTemplate)
    }

    @Test
    fun `sendGameState should broadcast full game state to correct topic`() {
        val state = GameStateDto(
            players = listOf(),
            board = listOf()
        )

        notificationService.sendGameState("LOBBY1", state)

        verify(messagingTemplate).convertAndSend(
            "/topic/game/state/LOBBY1",
            state
        )
    }

    @Test
    fun `sendPlayerMoved should broadcast move event to correct topic`() {
        notificationService.sendPlayerMoved("Alice", 3)

        val expected = mapOf("player" to "Alice", "position" to 3)
        verify(messagingTemplate).convertAndSend(
            "/topic/game/move",
            expected
        )
    }

    @Test
    fun `sendPlayerLanded should broadcast landing event to correct topic`() {
        notificationService.sendPlayerLanded("Bob", 5)

        val expected = mapOf("player" to "Bob", "position" to 5)
        verify(messagingTemplate).convertAndSend(
            "/topic/game/land",
            expected
        )
    }

    @Test
    fun `sendPlayerStatus should broadcast full status to user topic`() {
        val player = Player(
            name = "TestUser",
            position = 6,
            balance = 70000,
            money = mutableMapOf(5000 to 6, 10000 to 2)
        )

        notificationService.sendPlayerStatus(player)

        val expectedPayload: Map<String, Any?> = mapOf(
            "name" to "TestUser",
            "position" to 6,
            "balance" to 70000,
            "money" to mapOf(5000 to 6, 10000 to 2),
            "isTurn" to false
        )

        verify(messagingTemplate).convertAndSend(
            "/topic/player/TestUser/status",
            expectedPayload
        )
    }
    @Test
    fun `sendGameStateFromModels should convert and send DTOs`() {
        // Arrange: create a sample player and board cell
        val player = Player(name = "TestUser", position = 2, balance = 50000, isTurn = true)
        val boardCell = at.mankomania.server.model.BoardCell(index = 2, hasBranch = true)

        // Act: call the function
        notificationService.sendGameStateFromModels("LOBBY2", listOf(player), listOf(boardCell))

        // Assert: capture and inspect the argument
        val captor = ArgumentCaptor.forClass(Any::class.java)
        verify(messagingTemplate).convertAndSend(eq("/topic/game/state/LOBBY2"), captor.capture())

        // Verify that the sent object is a GameStateDto and contains our test player
        val sent = captor.value
        assertTrue(sent is GameStateDto)
        val dto = sent as GameStateDto
        assertEquals("TestUser", dto.players.first().name)
        assertEquals(2, dto.board.first().index)
        assertTrue(dto.board.first().hasBranch)
    }

    @Test
    fun `sendPlayerMoved should send correct player and edge position`() {
        notificationService.sendPlayerMoved("EdgePlayer", 0)
        val expected = mapOf("player" to "EdgePlayer", "position" to 0)
        verify(messagingTemplate).convertAndSend("/topic/game/move", expected)
    }

    @Test
    fun `sendPlayerLanded should send landing at last field`() {
        notificationService.sendPlayerLanded("TopPlayer", 39)
        val expected = mapOf("player" to "TopPlayer", "position" to 39)
        verify(messagingTemplate).convertAndSend("/topic/game/land", expected)
    }
}