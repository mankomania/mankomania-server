package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.springframework.stereotype.Service

data class Lobby(
    val lobbyId: String,
    val players: MutableList<Player> = mutableListOf()
)

@Service
class LobbyService {
    private val lobbies = mutableMapOf<String, Lobby>()

    fun createLobby(lobbyId: String, hostName: String): Lobby {
        val player = Player(hostName)
        val lobby = Lobby(lobbyId = lobbyId, players = mutableListOf(player))
        lobbies[lobbyId] = lobby
        return lobby
    }

    fun joinLobby(lobbyId: String, playerName: String): Boolean {
        val lobby = lobbies[lobbyId]
        if (lobby == null || lobby.players.size >= 4) return false
        lobby.players.add(Player(playerName))
        return true
    }

    fun getPlayers(lobbyId: String): List<Player> {
        return lobbies[lobbyId]?.players ?: emptyList()
    }
}

