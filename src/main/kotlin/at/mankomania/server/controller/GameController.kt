/**
 * @file GameController.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Manages core Mankomania game logic, including player turns,
 * movement (with branching), and execution of cell actions.
 */

package at.mankomania.server.controller

import at.mankomania.server.model.Board
import at.mankomania.server.model.Player
import at.mankomania.server.service.BankService
import at.mankomania.server.service.NotificationService


class GameController(
    private val board: Board,
    private val players: List<Player>,
    private val bankService: BankService = BankService(),
    private val notificationService: NotificationService
) {

    private var currentPlayerIndex = 0
    /**
     * Called once when a game session starts.
     */
    fun startGame() {
        // Broadcast initial state
        notificationService.sendGameState(players)
    }

    /**
     * Rolls/moves the given player and handles branching or landing.
     */
    fun movePlayer(playerId: String, steps: Int) {
        val player = players.find { it.name == playerId } ?: return
        val branched = player.move(steps, board)
        if (!branched) landOnCell(playerId, player.position)
        notificationService.sendPlayerMoved(playerId, player.position)
    }

    /**
     * Executes cell action and notifies clients.
     */
    fun landOnCell(playerId: String, cellIndex: Int) {
        val player = players.find { it.name == playerId } ?: return
        board.getCell(cellIndex).landOn(player, this)
        notificationService.sendPlayerLanded(playerId, cellIndex)
    }

}