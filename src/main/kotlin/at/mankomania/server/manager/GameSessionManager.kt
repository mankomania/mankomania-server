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
import at.mankomania.server.service.BankService
import at.mankomania.server.service.NotificationService
import at.mankomania.server.service.StartingMoneyAssigner
import at.mankomania.server.websocket.PlayerSocketService
import org.springframework.stereotype.Service

/**
 * Manages player joining, session startup, and active game controllers.
 */
@Service
class GameSessionManager(
    private val moneyAssigner: StartingMoneyAssigner,
    private val bankService: BankService,
    private val notificationService: NotificationService,
    private val playerSocketService: PlayerSocketService
) {
    private val activeGames = mutableMapOf<String, GameController>()
    private val gamePlayers = mutableMapOf<String, MutableList<Player>>()

    /**
     * A player joins a lobby. Returns false if name is duplicated or max reached (4).
     */
    fun joinGame(gameId: String, playerName: String): Boolean {
        val players = gamePlayers.getOrPut(gameId) { mutableListOf() }
        if (players.any { it.name == playerName } || players.size >= 4) return false
        players.add(Player(name = playerName))
        return true
    }

    /**
     * Returns the list of players currently in the lobby.
     */
    fun getPlayers(gameId: String): List<Player> =
        gamePlayers[gameId]?.toList() ?: emptyList()

    /**
     * Starts a session if 2–4 players are present. On success:
     * 1. Assign starting money
     * 2. Calculate and set fair start positions
     * 3. Build the board and create a GameController
     * 4. Start the game
     * Returns the list of start positions, or null on failure.
     */
    fun startSession(gameId: String, size: Int): List<Int>? {
        val players = gamePlayers[gameId]?.toList() ?: return null
        if (players.size < 2) return null

        // 1) Assign starting money
        moneyAssigner.assignToAll(players)

        // 2) Calculate fair, evenly spaced start positions
        val startPositions = (0 until players.size).map { i -> (i * size) / players.size }
        players.forEachIndexed { idx, player -> player.position = startPositions[idx] }

        // 3) Create board and controller
        val board = BoardFactory.createBoard(size) { idx -> idx % 10 == 0 }
        val controller = GameController(gameId, board, players, bankService, notificationService)
        activeGames[gameId] = controller

        // 4) Start the game
        controller.startGame()

        return startPositions
    }

    /**
     * Retrieves the GameController for an active session, or null if none exists.
     */
    fun getGameController(gameId: String): GameController? =
        activeGames[gameId]
}
