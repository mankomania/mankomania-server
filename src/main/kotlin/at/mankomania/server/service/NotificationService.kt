/**
 * @file NotificationService.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Sends game updates (state, moves, landings) to clients via WebSocket/STOMP.
 */
package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/**
 * Sends game updates to clients via WebSocket (STOMP).
 */
@Service
class NotificationService(private val messagingTemplate: SimpMessagingTemplate) {
    fun sendGameState(players: List<Player>) {
        messagingTemplate.convertAndSend("/topic/game/state", players)
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
}
