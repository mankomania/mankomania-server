package at.mankomania.server.controller.dto.lobby

data class LobbyJoinRequest(
    val lobbyId: String,
    val playerName: String
)