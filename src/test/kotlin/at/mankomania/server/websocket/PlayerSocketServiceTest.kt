package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import org.junit.Test
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate
import kotlin.test.assertEquals

class PlayerSocketServiceTest {

    // Dummy template to capture values
    class DummyMessagingTemplate : SimpMessagingTemplate(StubMessageChannel()) {
        var lastDestination: String? = null
        var lastPayload: Any? = null

        override fun convertAndSend(destination: String, payload: Any) {
            lastDestination = destination
            lastPayload = payload
        }
    }

    // Stub message channel required by SimpMessagingTemplate constructor
    class StubMessageChannel : MessageChannel {
        override fun send(message: Message<*>): Boolean = true
        override fun send(message: Message<*>, timeout: Long): Boolean = true
    }

    @Test
    fun sendFinancialState_should_send_correct_topic_and_money() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(name = "Player1", money = mutableMapOf(5000 to 10))

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player1/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_handle_empty_player_name() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(name = "", money = mutableMapOf(10000 to 2))

        service.sendFinancialState(player)

        assertEquals("/topic/player//money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_format_topic_correctly() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(name = "X Æ A-12", money = mutableMapOf(100 to 1))

        service.sendFinancialState(player)

        assertEquals("/topic/player/X Æ A-12/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_handle_empty_money_map() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(name = "EmptyMoney", money = mutableMapOf())

        service.sendFinancialState(player)

        assertEquals("/topic/player/EmptyMoney/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_accept_negative_money_values() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(name = "DebtGuy", money = mutableMapOf(5000 to -3))

        service.sendFinancialState(player)

        assertEquals("/topic/player/DebtGuy/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    // New test cases

    @Test
    fun sendFinancialState_should_handle_large_money_values() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
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
    }

    @Test
    fun sendFinancialState_should_handle_special_characters_in_name() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(
            name = "Player!@#$%^&*()",
            money = mutableMapOf(1000 to 5)
        )

        service.sendFinancialState(player)

        assertEquals("/topic/player/Player!@#$%^&*()/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }

    @Test
    fun sendFinancialState_should_handle_multiple_denominations() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(
            name = "MultiDenomPlayer",
            money = mutableMapOf(
                1 to 5,
                5 to 3,
                10 to 2,
                50 to 1,
                100 to 4
            )
        )

        service.sendFinancialState(player)

        assertEquals("/topic/player/MultiDenomPlayer/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }
}