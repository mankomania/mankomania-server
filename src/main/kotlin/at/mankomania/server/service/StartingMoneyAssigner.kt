package at.mankomania.server.service

import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import org.springframework.stereotype.Service

@Service
class StartingMoneyAssigner(
    private val playerSocketService: PlayerSocketService
) {

    private val denominations: Map<Int, Int> = mapOf(
        5_000 to 10,
        10_000 to 5,
        50_000 to 4,
        100_000 to 7
    )

    private val totalAmount: Int = denominations.entries.sumOf { it.key * it.value }

    /**
     * Assigns the predefined banknotes to the player only if balance is 0.
     * Idempotent: won't reassign if already set.
     */
    fun assign(player: Player) {
        if (player.balance > 0) {
            println("Player ${player.name} already has money. Skipping.")
            return
        }

        player.balance = totalAmount
        player.money = denominations.toMutableMap()
    }

    /**
     * Assigns money to all players in a list and notifies via WebSocket.
     */
    fun assignToAll(players: List<Player>) {
        for (player in players) {
            assign(player)
            playerSocketService.sendFinancialState(player)
        }
    }
}