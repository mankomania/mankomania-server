/**
 * @file NotificationService.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Sends game updates (state, moves, landings) to clients via WebSocket/STOMP.
 */
package at.mankomania.server.service

import at.mankomania.server.controller.dto.GameStartedDto
import at.mankomania.server.controller.dto.GameStateDto
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import at.mankomania.server.model.Player

/**
 * Sends game updates to clients via WebSocket (STOMP).
 */
@Service
class NotificationService(private val messagingTemplate: SimpMessagingTemplate) {

    /* Full snapshot – players + board */
    fun sendGameState(lobbyId: String, state: GameStateDto) {
        messagingTemplate.convertAndSend("/topic/game/state/$lobbyId", state)
    }

    fun sendPlayerMoved(playerId: String, position: Int) {
        messagingTemplate.convertAndSend(
            "/topic/game/move",
            mapOf("player" to playerId, "position" to position)
        )
    }

    fun sendPlayerLanded(playerId: String, position: Int) {
        messagingTemplate.convertAndSend(
            "/topic/game/land",
            mapOf("player" to playerId, "position" to position)
        )
    }
    /**
     * Sends the full player status over WebSocket.
     * Includes name, position, balance, and money (denominations).
     *
     * @param player the player whose full state should be sent
     */
    fun sendPlayerStatus(player: Player) {
        val destination = "/topic/player/${player.name}/status"
        val payload: Map<String, Any?> = mapOf(
            "name" to player.name,
            "position" to player.position,
            "balance" to player.balance,
            "money" to player.money
        )
        messagingTemplate.convertAndSend(destination, payload)
    }

    /**
     * Sends the “game started” snapshot to every client in the lobby.
     *
     * STOMP destination: `/topic/game/started/{lobbyId}`
     *
     * @param lobbyId the identifier of the lobby / game session
     * @param dto     the GameStartedDto with startPositions and firstPlayerIndex
     */
    fun sendGameStarted(lobbyId: String, dto: GameStartedDto) {
        messagingTemplate.convertAndSend("/topic/game/started/$lobbyId", dto)
    }

}
