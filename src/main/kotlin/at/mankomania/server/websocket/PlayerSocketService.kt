package at.mankomania.server.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import at.mankomania.server.model.Player

class PlayerSocketService(private val messagingTemplate: SimpMessagingTemplate) {

    fun sendFinancialState(player: Player) {
        val destination = "/topic/player/${player.name}/money"
        val moneyMap = player.money ?: mutableMapOf()
        messagingTemplate.convertAndSend(destination, moneyMap)
    }
}