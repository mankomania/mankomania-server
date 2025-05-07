package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate

class PlayerSocketServiceTest {

    inner class DummyMessagingTemplate : SimpMessagingTemplate(StubMessageChannel()) {
        var lastDestination: String? = null
        var lastPayload: Any? = null

        override fun convertAndSend(destination: String, payload: Any) {
            lastDestination = destination
            lastPayload = payload
        }
    }

    private class StubMessageChannel : MessageChannel {
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
    fun sendFinancialState_should_handle_empty_money() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val player = Player(name = "NoMoneyPlayer", money = mutableMapOf()) //

        service.sendFinancialState(player)

        assertEquals("/topic/player/NoMoneyPlayer/money", dummyTemplate.lastDestination)
        assertEquals(player.money, dummyTemplate.lastPayload)
    }
    @Test
    fun sendFinancialState_should_handle_large_money_map() {
        val dummyTemplate = DummyMessagingTemplate()
        val service = PlayerSocketService.PlayerSocketService(dummyTemplate)
        val moneyMap = mutableMapOf(
            5000 to 100,
            10000 to 50,
            50000 to 20,
            100000 to 10
        )
        val player = Player(name = "RichieRich", money = moneyMap)

        service.sendFinancialState(player)

        assertEquals("/topic/player/RichieRich/money", dummyTemplate.lastDestination)
        assertEquals(moneyMap, dummyTemplate.lastPayload)
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
}

