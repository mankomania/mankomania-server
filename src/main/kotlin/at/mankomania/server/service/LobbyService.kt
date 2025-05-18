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
        if (lobby.players.any { it.name == playerName }) {
            println("🔁 Spieler '$playerName' ist bereits in Lobby '$lobbyId' – wird ignoriert.")
            return true
        }
        lobby.players.add(Player(playerName))
        return true
    }

    fun getPlayers(lobbyId: String): List<Player> {
        return lobbies[lobbyId]?.players ?: emptyList()
    }
    fun getLobby(lobbyId: String): Lobby? {
        return lobbies[lobbyId]
    }

}

