package at.mankomania.server.websocket

import at.mankomania.server.controller.dto.LobbyMessage
import at.mankomania.server.controller.dto.LobbyResponse
import at.mankomania.server.service.LobbyService
import at.mankomania.server.manager.GameSessionManager
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Controller
class LobbyWebSocketController(
    private val lobbyService: LobbyService,
    private val sessionManager: GameSessionManager
) {
    private val logger = LoggerFactory.getLogger(LobbyWebSocketController::class.java)

    @MessageMapping("/lobby")
    @SendTo("/topic/lobby")
    fun handleLobbyAction(@Payload message: LobbyMessage): LobbyResponse {
        logger.info("Received lobby message: $message")

        return when (message.type) {
            "create" -> {
                // Create in both lobbyService and sessionManager
                val lobby = lobbyService.createLobby(message.lobbyId!!, message.playerName)
                sessionManager.joinGame(lobby.lobbyId, message.playerName)
                val names = lobby.players.map { it.name }
                LobbyResponse("created", lobby.lobbyId, message.playerName, names.size, names)
            }

            "join" -> {
                // Join in both lobbyService and sessionManager
                val okService = lobbyService.joinLobby(message.lobbyId!!, message.playerName)
                val okSession = sessionManager.joinGame(message.lobbyId, message.playerName)
                val players = lobbyService.getPlayers(message.lobbyId).map { it.name }
                LobbyResponse(
                    type = if (okService && okSession) "joined" else "join-failed",
                    lobbyId = message.lobbyId,
                    playerName = message.playerName,
                    playerCount = if (okService && okSession) players.size else null,
                    players = if (okService && okSession) players else null
                )
            }

            "start" -> {
                logger.info("🔔 Game started in lobby ${message.lobbyId} by ${message.playerName}")

                // start the session
                sessionManager.startSession(message.lobbyId!!, /* boardSize = */ 20)

                // prepare response to client
                val names = lobbyService.getPlayers(message.lobbyId).map { it.name }
                val response = LobbyResponse("start", message.lobbyId, message.playerName, names.size, names)

                // Delay initial GameState broadcast by 200ms so client subscription can be set up
                Executors.newSingleThreadScheduledExecutor().schedule({
                    sessionManager.getGameController(message.lobbyId)?.startGame()
                }, 200, TimeUnit.MILLISECONDS)

                return response
            }

            else -> LobbyResponse("error", message.lobbyId ?: "unknown", message.playerName)
        }
    }
}
