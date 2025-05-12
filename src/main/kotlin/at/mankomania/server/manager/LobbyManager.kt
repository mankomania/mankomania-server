package at.mankomania.server.manager

import at.mankomania.server.model.Player

data class Lobby(
    val id: String,
    val players: MutableList<Player>
)

object LobbyManager {
    private val lobbies = mutableMapOf<String, Lobby>()

    fun createLobby(player: Player): Lobby {
        val id = generateLobbyId()
        val lobby = Lobby(id, mutableListOf(player))
        lobbies[id] = lobby
        return lobby
    }

    fun joinLobby(id: String, player: Player): Lobby? {
        val lobby = lobbies[id]
        lobby?.players?.add(player)
        return lobby
    }

    fun getLobby(id: String): Lobby? = lobbies[id]

    private fun generateLobbyId(): String {
        val chars = ('A'..'Z') + ('0'..'9')
        var id: String
        do {
            id = (1..4).map { chars.random() }.joinToString("")
        } while (lobbies.containsKey(id))
        return id
    }
}
