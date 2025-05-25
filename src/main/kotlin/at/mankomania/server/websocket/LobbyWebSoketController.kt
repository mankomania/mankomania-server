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
                val lobby = lobbyService.createLobby(message.lobbyId!!, message.playerName)
                val names = lobby.players.map { it.name }
                LobbyResponse("created", lobby.lobbyId, message.playerName, lobby.players.size, names)
            }

            "join" -> {
                val ok = lobbyService.joinLobby(message.lobbyId!!, message.playerName)
                val names = lobbyService.getPlayers(message.lobbyId).map { it.name }
                LobbyResponse(
                    type       = if (ok) "joined" else "join-failed",
                    lobbyId    = message.lobbyId,
                    playerName = message.playerName,
                    playerCount= if (ok) names.size else null,
                    players    = if (ok) names else null
                )
            }

            "start" -> {
                logger.info("🔔 Game started in lobby ${message.lobbyId} by ${message.playerName}")

                // 1) Spiel-Session initialisieren
                val boardSize = 20  // falls variabel: aus DTO übergeben
                sessionManager.startSession(message.lobbyId!!, boardSize)

                // 2) Erstes GameStateDto broadcasten
                val controller = sessionManager.getGameController(message.lobbyId)!!
                controller.startGame()

                // 3) LobbyResponse zurück, damit der Client wechselt
                val names = lobbyService.getPlayers(message.lobbyId).map { it.name }
                LobbyResponse("start", message.lobbyId, message.playerName, names.size, names)
            }

            else -> LobbyResponse("error", message.lobbyId ?: "unknown", message.playerName)
        }
    }
}