package at.mankomania.server.model

data class LobbyInfo(
    val lobbyId: String,
    val players: MutableList<String> = mutableListOf()
)
