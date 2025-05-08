package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/**
 * Service class responsible for handling WebSocket communication related to player financial state.
 */
@Service
class PlayerSocketService(private val messagingTemplate: SimpMessagingTemplate) {

    /**
     * Sends the current financial state of the given player via WebSocket.
     * The message is sent to the topic: /topic/player/{playerName}/money
     *
     * @param player the player whose financial state should be sent
     */
    fun sendFinancialState(player: Player) {
        val destination = "/topic/player/${player.name}/money"
        val moneySnapshot = player.money?.toMap()
        if (moneySnapshot != null) {
            messagingTemplate.convertAndSend(destination, moneySnapshot)
        }
    }
}