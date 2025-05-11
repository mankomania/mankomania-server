package at.mankomania.server.websocket

import at.mankomania.server.controller.dto.LobbyMessage
import at.mankomania.server.controller.dto.LobbyResponse
import at.mankomania.server.service.LobbyService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class LobbyWebSocketController(
    private val lobbyService: LobbyService
) {
    private val logger = LoggerFactory.getLogger(LobbyWebSocketController::class.java)

    @MessageMapping("/lobby") // z.B. /app/lobby
    @SendTo("/topic/lobby")   // z.B. /topic/lobby für alle Clients
    fun handleLobbyAction(@Payload message: LobbyMessage): LobbyResponse {
        logger.info("Received lobby message: $message")

        return when (message.type) {
            "create" -> {
                val lobby = lobbyService.createLobby(message.lobbyId!!, message.playerName)
                LobbyResponse(
                    type = "created",
                    lobbyId = lobby.lobbyId,
                    playerName = message.playerName,
                    playerCount = lobby.players.size
                )
            }

            "join" -> {
                val success = lobbyService.joinLobby(message.lobbyId!!, message.playerName)
                val playerCount = lobbyService.getPlayers(message.lobbyId).size
                LobbyResponse(
                    type = if (success) "joined" else "join-failed",
                    lobbyId = message.lobbyId,
                    playerName = message.playerName,
                    playerCount = if (success) playerCount else null
                )
            }

            else -> LobbyResponse(
                type = "error",
                lobbyId = message.lobbyId ?: "unknown",
                playerName = message.playerName,
                playerCount = null
            )
        }
    }
}
