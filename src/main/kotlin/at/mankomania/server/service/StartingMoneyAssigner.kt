import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService

class StartingMoneyAssigner(
    private val playerSocketService: PlayerSocketService? = null
) {
    private val denominations = mapOf(
        5_000 to 10,
        10_000 to 5,
        50_000 to 4,
        100_000 to 7
    )

    private val totalAmount = denominations.entries.sumOf { it.key * it.value }
    fun assign(player: Player) {
        if (player.balance > 0) {
            println("Player ${player.name} already has money. Skipping.")
            return
        }

        player.balance = totalAmount
        player.money = denominations.toMutableMap()
        playerSocketService?.sendFinancialState(player)
    }

    fun assignToAll(players: List<Player>) {
        players.forEach { assign(it) }
    }
}