/**
 * @file NotificationService.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Sends game updates (state, moves, landings) to clients via WebSocket/STOMP.
 */
package at.mankomania.server.service

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

    // Helper to convert server Player to PlayerDto
    private fun toPlayerDto(player: Player): at.mankomania.server.controller.dto.PlayerDto {
        return at.mankomania.server.controller.dto.PlayerDto(
            name = player.name,
            position = player.position,
            balance = player.balance,
            isTurn = player.isTurn
        )
    }

    // Helper to convert BoardCell to CellDto
    private fun toCellDto(cell: at.mankomania.server.model.BoardCell): at.mankomania.server.controller.dto.CellDto {
        return at.mankomania.server.controller.dto.CellDto(
            index = cell.index,
            hasBranch = cell.hasBranch
        )
    }

    /**
     * Sends the game state using server models (Player, BoardCell), mapping to DTOs for the client.
     */
    fun sendGameStateFromModels(lobbyId: String, players: List<Player>, boardCells: List<at.mankomania.server.model.BoardCell>) {
        val playerDtos = players.map { toPlayerDto(it) }
        val cellDtos = boardCells.map { toCellDto(it) }
        val state = at.mankomania.server.controller.dto.GameStateDto(playerDtos, cellDtos)
        sendGameState(lobbyId, state)
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
     * Includes name, position, balance, money (denominations), and isTurn.
     *
     * @param player the player whose full state should be sent
     */
    fun sendPlayerStatus(player: Player) {
        val destination = "/topic/player/${player.name}/status"
        val payload: Map<String, Any?> = mapOf(
            "name" to player.name,
            "position" to player.position,
            "balance" to player.balance,
            "money" to player.money,
            "isTurn" to player.isTurn
        )
        messagingTemplate.convertAndSend(destination, payload)
    }
}
