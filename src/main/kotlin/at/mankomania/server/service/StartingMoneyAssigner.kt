import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import org.springframework.stereotype.Service

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
     * Assigns money to the player only if they don't already have a balance.
     * Sends the updated financial state via WebSocket if money is assigned.
     */
    fun assign(player: Player) {
        if (player.balance > 0) {
            println("Player ${player.name} already has money. Skipping.")
            return
        }

        player.balance = totalAmount
        player.money = denominations.toMutableMap()
        playerSocketService.sendFinancialState(player) // Send the financial state to the player
    }

    /**
     * Assigns money to all players in the list.
     */
    fun assignToAll(players: List<Player>) {
        players.forEach { assign(it) }
    }
}