/**
 * @file GameSessionManager.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Manages game sessions, including creating new games and managing active sessions.
 */

package at.mankomania.server.manager
import at.mankomania.server.controller.GameController
import at.mankomania.server.model.BoardFactory
import at.mankomania.server.model.Player
import at.mankomania.server.service.StartingMoneyAssigner

/**
 * Manages player joining, session startup, and active game controllers.
 */
class GameSessionManager {
    private val activeGames = mutableMapOf<String, GameController>()
    private val gamePlayers = mutableMapOf<String, MutableList<Player>>()
    private val moneyAssigner = StartingMoneyAssigner()

    /**
     * A player joins a lobby. Returns false if name duplicated or max reached.
     */
    fun joinGame(gameId: String, playerName: String): Boolean {
        val players = gamePlayers.getOrPut(gameId) { mutableListOf() }
        if (players.any { it.name == playerName } || players.size >= 4) return false
        players.add(Player(name = playerName))
        return true
    }

    /**
     * List of players currently in lobby.
     */
    fun getPlayers(gameId: String): List<Player> =
        gamePlayers[gameId]?.toList() ?: emptyList()

    /**
     * Starts a session if 2–4 players are present, assigns money and positions, creates the game.
     * Returns the list of start positions, or null on failure.
     */
    fun startSession(gameId: String, size: Int): List<Int>? {
        val players = gamePlayers[gameId] ?: return null
        if (players.size < 2) return null

        // Assign starting money
        moneyAssigner.assignToAll(players)

        // Calculate fair, evenly spaced start positions
        val startPositions = (0 until players.size).map { i -> (i * size) / players.size }
        players.forEachIndexed { idx, player -> player.position = startPositions[idx] }

        // Create board and controller
        val board = BoardFactory.createBoard(size) { idx -> idx % 10 == 0 }
        val controller = GameController(board, players)
        activeGames[gameId] = controller
        controller.startGame()

        return startPositions
    }

    /**
     * Retrieves the GameController for an active session.
     */
    fun getGameController(gameId: String): GameController? = activeGames[gameId]
}