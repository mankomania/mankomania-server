/**
 * @file GameSessionManager.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Manages game sessions, including creating new games and managing active sessions.
 */

package at.mankomania.server.manager
import at.mankomania.server.controller.GameController
import at.mankomania.server.controller.dto.GameStartedDto
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
     * Starts a new game session once 2–4 players are present.
     *
     * Workflow
     * 1. Assign starting money to every player.
     * 2. Calculate evenly spaced start positions based on `boardSize`.
     * 3. Pick the first player randomly (`firstPlayerIndex`).
     * 4. Build the board with the requested size and register a GameController.
     * 5. Broadcast a `GameStartedDto` (startPositions + firstPlayerIndex) to all clients.
     * 6. Broadcast the initial full `GameStateDto` so UIs can render the board.
     *
     * @param gameId     the lobby / game identifier
     * @param boardSize  desired number of cells for the board
     * @return the list of start positions, or `null` if the session cannot be started
     */
    fun startSession(gameId: String, boardSize: Int): Pair<List<Int>, Int>? {
        val players = gamePlayers[gameId]?.toList() ?: return null
        if (players.size < 2) return null

        // 1) assign starting money
        moneyAssigner.assignToAll(players)

        // 2) start positions (evenly spaced)
        val startPositions = (players.indices).map { idx -> (idx * boardSize) / players.size }
        players.forEachIndexed { idx, p -> p.position = startPositions[idx] }

        // 3) determine first player randomly
        val firstIdx = kotlin.random.Random.nextInt(players.size)

        // 4) create board & controller
        val board = BoardFactory.createBoard(boardSize) { i -> i % 10 == 0 }
        val controller = GameController(gameId, board, players, bankService, notificationService)
        activeGames[gameId] = controller

        // 5) broadcast GameStartedDto
        val dto = GameStartedDto(gameId, startPositions, firstIdx)
        notificationService.sendGameStarted(gameId, dto)

        // 6) initial GameState will be sent by WebSocket-controller after 200 ms
        return startPositions to firstIdx
    }

    /**
     * Retrieves the GameController for an active session, or null if none exists.
     */
    fun getGameController(gameId: String): GameController? =
        activeGames[gameId]
}
