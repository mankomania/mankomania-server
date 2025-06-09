package at.mankomania.server.controller.dto

data class LobbyMessage(
    val type: String,
    val playerName: String,
    val lobbyId: String? = null,
    val boardSize: Int? = null
)
