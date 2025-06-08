/**
 * @file GameController.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Manages core Mankomania game logic, including player turns,
 * movement (with branching), and execution of cell actions.
 */

package at.mankomania.server.controller

import at.mankomania.server.controller.dto.GameStateDto
import at.mankomania.server.model.Board
import at.mankomania.server.model.MoveResult
import at.mankomania.server.model.Player
import at.mankomania.server.service.BankService
import at.mankomania.server.service.NotificationService
import at.mankomania.server.controller.dto.PlayerDto


class GameController(
    private val gameId: String,
    private val board: Board,
    private val players: List<Player>,
    private val bankService: BankService = BankService(), //future action
    private val notificationService: NotificationService,
    private var currentPlayerIndex: Int = 0

) {


    /**
     * Called once when a game session starts.
     */
    fun startGame() {
        // Broadcast full game state (players + board)
        currentPlayerIndex = 0
        val currentPlayer:Player = players[currentPlayerIndex]
        val state = GameStateDto(
            players = players.map { PlayerDto(it.name, it.position) },
            board = board.cells,
            currentPlayer = currentPlayer.name
        )
        notificationService.sendGameState(gameId, state)
    }

    /**
     * Rolls/moves the given player and handles branching or landing.
     */
    fun movePlayer(playerId: String, steps: Int) {
        val player = players.find { it.name == playerId } ?: return
        val branched = player.move(steps, board)
        if (!branched) landOnCell(playerId, player.position)
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        val nextPlayer: Player = players[currentPlayerIndex]
        notificationService.sendPlayerMoved(playerId, player.position)
        notificationService.sendPlayerStatus(player)
        val updatedState = GameStateDto(
            players = players.map { PlayerDto(it.name, it.position) },
            board = board.cells,
            currentPlayer = nextPlayer.name
        )
        notificationService.sendGameState(gameId, updatedState)
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
     * Retrieves the Player object by ID for state updates (e.g., recording dice history).
     *
     * @param playerId the identifier of the player
     * @return the Player instance or null if not found
     */
    fun getPlayer(playerId: String): Player? = players.find { it.name == playerId }

    /**
     * Executes cell action and notifies clients.
     */
    fun landOnCell(playerId: String, cellIndex: Int) {
        val player = players.find { it.name == playerId } ?: return
        board.getCell(cellIndex).landOn(player, this)
        notificationService.sendPlayerLanded(playerId, cellIndex)
        notificationService.sendPlayerStatus(player)
    }

}