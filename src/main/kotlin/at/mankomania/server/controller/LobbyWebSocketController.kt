package at.mankomania.server.controller

import at.mankomania.server.controller.dto.lobby.LobbyJoinRequest
import at.mankomania.server.controller.dto.lobby.LobbyRequest
import at.mankomania.server.controller.dto.lobby.LobbyUpdate
import at.mankomania.server.manager.LobbyManager
import at.mankomania.server.model.Player
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class LobbyWebSocketController(
    private val messagingTemplate: SimpMessagingTemplate
) {

    @MessageMapping("/create-lobby")
    fun createLobby(
        @Payload message: LobbyRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val player = Player(message.playerName)
        val lobby = LobbyManager.createLobby(player)

        headerAccessor.sessionAttributes?.put("playerName", player.name)
        headerAccessor.sessionAttributes?.put("lobbyId", lobby.id)

        messagingTemplate.convertAndSend(
            "/topic/lobby/${lobby.id}",
            LobbyUpdate(lobby.id, lobby.players.map { it.name })
        )
    }

    @MessageMapping("/join-lobby")
    fun joinLobby(
        @Payload message: LobbyJoinRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val player = Player(message.playerName)
        val lobby = LobbyManager.getLobby(message.lobbyId)

        if (lobby == null) {
            val sessionId = headerAccessor.sessionId ?: return
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                "Lobby not found"
            )
            return
        }

        if (lobby.players.size >= 4) {
            val sessionId = headerAccessor.sessionId ?: return
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                "Lobby full"
            )
            return
        }

        lobby.players.add(player)

        headerAccessor.sessionAttributes?.put("playerName", player.name)
        headerAccessor.sessionAttributes?.put("lobbyId", lobby.id)

        messagingTemplate.convertAndSend(
            "/topic/lobby/${lobby.id}",
            LobbyUpdate(lobby.id, lobby.players.map { it.name })
        )
    }
}
