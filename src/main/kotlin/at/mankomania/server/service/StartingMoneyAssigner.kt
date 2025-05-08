package at.mankomania.server.service

import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import org.springframework.stereotype.Service

/**
 * Service responsible for assigning starting money to players.
 * Sends a WebSocket update if money is assigned.
 */
@Service
class StartingMoneyAssigner(
    private val playerSocketService: PlayerSocketService
) {
    private val denominations = mapOf(
        5_000 to 10,
        10_000 to 5,
        50_000 to 4,
        100_000 to 7
    )

    private val totalAmount = denominations.entries.sumOf { it.key * it.value }

    /**
     * Assigns starting money to a single player if they currently have no balance.
     * Also sends a WebSocket update to notify the client.
     */
    fun assign(player: Player) {
        if (player.balance > 0) return

        player.balance = totalAmount
        player.money = denominations.toMutableMap()

        playerSocketService.sendFinancialState(player)
    }

    /**
     * Assigns starting money to all players in the list.
     */
    fun assignToAll(players: List<Player>) {
        for (player in players) {
            assign(player)
        }
    }
}