package at.mankomania.server.service
/**
 * @file NotificationServiceTest.kt
 * @author eles17
 * @since 13.5.2025
 * @description Unit tests for NotificationService, verifying WebSocket message sending logic.
 */
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

    /**
    * Verifies that the sendPlayerStatus method correctly constructs the WebSocket message
    * with full player state and sends it to the appropriate destination topic.
    */
    @Test
    fun `sendPlayerStatus should push correct WebSocket message`() {
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

        Mockito.verify(messagingTemplate).convertAndSend("/topic/player/TestUser/status", expectedPayload)
    }
}