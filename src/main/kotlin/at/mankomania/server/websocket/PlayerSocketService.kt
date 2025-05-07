package at.mankomania.server.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import at.mankomania.server.model.Player
import org.springframework.stereotype.Service

/**
 * Service class responsible for handling WebSocket communication related to player financial state.
 * Uses Spring's SimpMessagingTemplate to send messages to specific WebSocket topics.
 *
 * @property messagingTemplate The Spring SIMP messaging template used for WebSocket communication
 */
@Service
class PlayerSocketService(private val messagingTemplate: SimpMessagingTemplate) {

    /**
     * Sends the financial state of a player to a specific WebSocket topic.
     * The topic is constructed using the player's name in the format: /topic/player/{playerName}/money
     *
     * @param player The player whose financial state needs to be sent
     */
    fun sendFinancialState(player: Player) {
        val destination = "/topic/player/${player.name}/money"
        val moneyMap = player.money?.toMap() ?: run {
            logger.warn("Player ${player.name} has no financial state (money is null). Sending an empty map.")
            emptyMap()
        }
        messagingTemplate.convertAndSend(destination, moneyMap)
    }
}