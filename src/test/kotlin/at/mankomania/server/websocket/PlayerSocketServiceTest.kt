package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate

class PlayerSocketServiceTest {

    class DummyMessagingTemplate : SimpMessagingTemplate(StubMessageChannel()) {
        var lastDestination: String? = null
        var lastPayload: Any? = null
        var messageCount: Int = 0

        override fun convertAndSend(destination: String, payload: Any) {
            lastDestination = destination
            lastPayload = payload
            messageCount++
        }
    }

    class StubMessageChannel : MessageChannel {
        override fun send(message: Message<*>): Boolean = true
        override fun send(message: Message<*>, timeout: Long): Boolean = true
    }

    @Test
    fun sendFinancialState_should_send_correct_topic_and_money() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Player1", money = mutableMapOf(5000 to 10))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player1/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_empty_player_name() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "", money = mutableMapOf(10000 to 2))

        service.sendFinancialState(player)

        assertEquals("/topic/player//money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_unicode_characters() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Æon", money = mutableMapOf(100 to 1))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Æon/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_handle_negative_values() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "DebtGuy", money = mutableMapOf(5000 to -3))

        service.sendFinancialState(player)

        assertEquals("/topic/player/DebtGuy/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_handle_special_characters() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Player!@#$", money = mutableMapOf(1000 to 5))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player!@#\$/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_handle_multiple_calls() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Multi", money = mutableMapOf(1000 to 5))

        repeat(3) { service.sendFinancialState(player) }

        assertEquals(3, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_emoji() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "😊", money = mutableMapOf(2000 to 3))

        service.sendFinancialState(player)

        assertEquals("/topic/player/😊/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun service_should_initialize_properly() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)

        assertNotNull(service)
    }
}