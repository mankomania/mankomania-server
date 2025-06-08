package at.mankomania.server.service

import at.mankomania.server.controller.dto.GameStateDto
import at.mankomania.server.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.messaging.simp.SimpMessagingTemplate

class NotificationServiceTest {

    private lateinit var messagingTemplate: SimpMessagingTemplate
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setup() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate::class.java)
        notificationService = NotificationService(messagingTemplate)
    }

    @Test
    fun `sendGameState should broadcast full game state to correct topic`() {
        val state = GameStateDto(
            players = listOf(),
            board = listOf(),
            currentTurnPlayerName = "Dummy"
        )

        notificationService.sendGameState("LOBBY1", state)

        Mockito.verify(messagingTemplate).convertAndSend(
            "/topic/game/state/LOBBY1",
            state
        )
    }

    @Test
    fun `sendPlayerMoved should broadcast move event to correct topic`() {
        notificationService.sendPlayerMoved("Alice", 3)

        val expected = mapOf("player" to "Alice", "position" to 3)
        Mockito.verify(messagingTemplate).convertAndSend(
            "/topic/game/move",
            expected
        )
    }

    @Test
    fun `sendPlayerLanded should broadcast landing event to correct topic`() {
        notificationService.sendPlayerLanded("Bob", 5)

        val expected = mapOf("player" to "Bob", "position" to 5)
        Mockito.verify(messagingTemplate).convertAndSend(
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
            "money" to mapOf(5000 to 6, 10000 to 2)
        )

        Mockito.verify(messagingTemplate).convertAndSend(
            "/topic/player/TestUser/status",
            expectedPayload
        )
    }
}