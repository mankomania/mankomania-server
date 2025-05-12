package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate
import kotlin.test.Test

/**
 * Test class for PlayerSocketService
 * Contains test cases for various scenarios of financial state transmission
 */
class PlayerSocketServiceTest {

    /**
     * Mock messaging template for testing
     * Captures the last message sent and counts total messages
     */
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

    /**
     * Stub implementation of MessageChannel for testing
     */
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
    fun `sendFinancialState should not send anything if player money is null`() {
        // Using the custom mock messaging template from your test class
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService(dummyTemplate)

        // Create a player with null money
        val player = Player(name = "Player2")
        player.money = null  // Setting money to null

        // Call the method
        service.sendFinancialState(player)

        // Verify that no message was sent
        assertEquals(0, dummyTemplate.messageCount)
        assertEquals(null, dummyTemplate.lastDestination)
        assertEquals(null, dummyTemplate.lastPayload)
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
        val player = Player(name = "Player😀", money = mutableMapOf(1000 to 5))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player😀/money", dummyTemplate.lastDestination)
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
