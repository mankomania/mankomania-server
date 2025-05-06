/**
 * @file GameSessionManager.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Manages game sessions, including creating new games and managing active sessions.
 */

package at.mankomania.server.manager
import at.mankomania.server.controller.GameController
import at.mankomania.server.model.BoardFactory

class GameSessionManager {
    private val activeGames = mutableMapOf<String, GameController>()

    /**
     * Creates a new game with a given ID and board size.
     *
     * @param gameId The ID of the new game.
     * @param size The number of fields on the board.
     */
    fun createGame(gameId: String, size: Int) {
        val board = BoardFactory.createBoard(size) { index -> index % 10 == 0 }  // Example: Every 10th cell has a branch
        val gameController = GameController(board)
        activeGames[gameId] = gameController
        gameController.startGame()
    }

    /**
     * Returns the GameController for a specific game.
     *
     * @param gameId The ID of the game.
     * @return The GameController for the given game.
     */
    fun getGameController(gameId: String): GameController? {
        return activeGames[gameId]
    }
}