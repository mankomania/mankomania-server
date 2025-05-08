/**
 * @file GameController.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Manages core Mankomania game logic, including player turns,
 * movement (with branching), and execution of cell actions.
 */

package at.mankomania.server.controller

import at.mankomania.server.model.Board
import at.mankomania.server.model.MoveResult
import at.mankomania.server.model.Player
import at.mankomania.server.service.BankService
import at.mankomania.server.service.NotificationService


class GameController(
    private val board: Board,
    private val players: List<Player>,
    private val bankService: BankService = BankService(), //future action
    private val notificationService: NotificationService
) {


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
    * Calculates the result of a player's move for the UI, including updated position,
    * field description, and other players on the same field.
    *
    * @param playerId The name of the player who is moving.
    * @param steps Number of steps the player will move.
    * @return A MoveResult DTO with position and field data, or null if player not found.
    **/
    fun computeMoveResult(playerId: String, steps: Int): MoveResult? {
        val player = players.find{ it.name == playerId} ?: return null
        val oldPos = player.position
        val branched = player.move(steps, board)
        if(!branched) landOnCell(playerId, player.position)

        val currentField = board.getCell(player.position)
        val others = players.filter { it.name != playerId && it.position == player.position }.map {it.name}

        return MoveResult(
            newPosition = player.position,
            oldPosition = oldPos,
            fieldType = currentField.action?.javaClass?.simpleName ?: "NoAction",
            fieldDescription = currentField.action?.description ?: "No description available",
            playersOnField = others
        )
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