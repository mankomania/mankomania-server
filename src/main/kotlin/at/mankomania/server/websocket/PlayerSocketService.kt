package at.mankomania.server.websocket

import at.mankomania.server.model.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/**
 * Service class responsible for handling WebSocket communication related to player financial state.
 */
@Service
class PlayerSocketService(private val messagingTemplate: SimpMessagingTemplate) {

    /**w
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

    /**
     * Broadcasts the current state of the given player to all clients.
     * Includes name, position, balance, and denomination map.
     * This allows all players to see up-to-date game states of others.
     *
     * @param player The player whose state should be broadcast.
     */
    fun broadcastPlayerStatus(player: Player) {
        val destination = "/topic/player/${player.name}/status"
        val state = mapOf(
            "name" to player.name,
            "position" to player.position,
            "balance" to player.balance,
            "money" to player.money
        )
        messagingTemplate.convertAndSend(destination, state)
    }
}