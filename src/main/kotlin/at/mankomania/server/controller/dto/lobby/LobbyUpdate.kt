package at.mankomania.server.controller.dto.lobby

data class LobbyUpdate(
    val lobbyId: String,
    val players: List<String>
)