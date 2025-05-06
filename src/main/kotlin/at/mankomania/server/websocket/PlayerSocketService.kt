package at.mankomania.server.websocket
import at.mankomania.server.model.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

class PlayerSocketService {

    @Service
    class PlayerSocketService(
        private val messagingTemplate: SimpMessagingTemplate
    ) {
        fun sendFinancialState(player: Player) {
            val topic = "/topic/player/${player.name}/money"
            messagingTemplate.convertAndSend(topic, player.money)
        }
    }
}

