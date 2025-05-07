package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Test
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
    fun sendFinancialState_should_format_topic_correctly_with_unicode() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Æon", money = mutableMapOf(100 to 1))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Æon/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_empty_money_map() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "EmptyMoney", money = mutableMapOf())

        service.sendFinancialState(player)

        assertEquals("/topic/player/EmptyMoney/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_accept_negative_money_values() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "DebtGuy", money = mutableMapOf(5000 to -3))

        service.sendFinancialState(player)

        assertEquals("/topic/player/DebtGuy/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_special_characters() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Player!@#$%^&*()", money = mutableMapOf(1000 to 5))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player!@#$%^&*()/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_whitespace() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "   Player   With   Spaces   ", money = mutableMapOf(1000 to 5))

        service.sendFinancialState(player)

        assertEquals("/topic/player/   Player   With   Spaces   /money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_multiple_messages() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "MultipleMessages", money = mutableMapOf(1000 to 5))

        service.sendFinancialState(player)
        service.sendFinancialState(player)
        service.sendFinancialState(player)

        assertEquals("/topic/player/MultipleMessages/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(3, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_emoji() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(name = "Player\uD83D\uDE00", money = mutableMapOf(1000 to 5))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player\uD83D\uDE00/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun sendFinancialState_should_handle_large_money_values() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)
        val player = Player(
            name = "RichPlayer",
            money = mutableMapOf(
                Int.MAX_VALUE to Int.MAX_VALUE,
                1000000000 to 1000000
            )
        )

        service.sendFinancialState(player)

        assertEquals("/topic/player/RichPlayer/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
        assertEquals(1, dummyTemplate.messageCount)
    }

    @Test
    fun messaging_template_should_be_properly_initialized() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)

        assertNotNull(service)
    }
}