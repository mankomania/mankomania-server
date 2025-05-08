import at.mankomania.server.model.Player
import at.mankomania.server.websocket.PlayerSocketService
import org.springframework.stereotype.Service

@Service
class StartingMoneyAssigner(
    private val playerSocketService: PlayerSocketService // Dependency for sending financial state updates via WebSocket
) {
    // Denominations of money to be assigned to players, along with their respective counts
    private val denominations = mapOf(
        5_000 to 10,   // 10 notes of 5,000
        10_000 to 5,   // 5 notes of 10,000
        50_000 to 4,   // 4 notes of 50,000
        100_000 to 7   // 7 notes of 100,000
    )

    // Calculate the total amount of money to be assigned by summing the value of each denomination * its count
    private val totalAmount = denominations.entries.sumOf { it.key * it.value }

    /**
     * Assigns money to a player only if they don't already have a balance.
     * Sends the updated financial state via WebSocket if money is assigned.
     *
     * @param player The player to assign money to
     */
    fun assign(player: Player) {
        // Check if the player already has money. If they do, skip the assignment
        if (player.balance > 0) {
            println("Player ${player.name} already has money. Skipping.")
            return
        }

        // Assign the total amount and set the money denominations
        player.balance = totalAmount
        player.money = denominations.toMutableMap()

        // Send the updated financial state to the player via WebSocket
        playerSocketService.sendFinancialState(player) // Notify the player about their new balance and money
    }

    /**
     * Assigns money to all players in the provided list.
     *
     * @param players List of players to assign money to
     */
    fun assignToAll(players: List<Player>) {
        // Loop through each player and assign money
        players.forEach { assign(it) }
    }
}