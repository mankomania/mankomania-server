import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate

class PlayerSocketServiceTest {

    // Dummy/fake implementation of SimpMessagingTemplate
    inner class DummyMessagingTemplate : SimpMessagingTemplate(StubMessageChannel()) {
        var lastDestination: String? = null
        var lastPayload: Any? = null

        override fun convertAndSend(destination: String, payload: Any) {
            lastDestination = destination
            lastPayload = payload
        }
    }

    // Simple stub implementation of MessageChannel
    private class StubMessageChannel : MessageChannel {
        override fun send(message: Message<*>): Boolean = true
        override fun send(message: Message<*>, timeout: Long): Boolean = true
    }

    @Test
    fun sendFinancialState_should_send_correct_topic_and_money() {
        // Arrange
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(
            name = "Player1",
            money = mutableMapOf(5000 to 10)
        )

        // Act
        service.sendFinancialState(player)

        // Assert
        assertEquals("/topic/player/Player1/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }
}