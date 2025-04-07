package at.mankomania.server.service

import at.mankomania.server.model.HorseColor
import at.mankomania.server.model.Bet
import at.mankomania.server.model.Player
import org.springframework.stereotype.Service

@Service
class HorseRaceServiceTest {

    private val players = mutableMapOf<String, Player>()

    // Function that simulates the race and returns a random HorseColor
    fun runRace(): HorseColor {
        return HorseColor.values().random()  // Returns a random color from the HorseColor enum
    }

    // Other functions (spinRoulette, calculateWinnings, etc.)
    fun spinRoulette(): HorseColor {
        return HorseColor.values().random()  // Returns a random color from the HorseColor enum
    }

    // Calculate the winnings based on the bets and the winning color
    fun calculateWinnings(bets: List<Bet>, winningColor: HorseColor): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        for (bet in bets) {
            if (bet.horseColor == winningColor) {  // Compare bet's horse color with the winning color
                results[bet.playerId] = bet.amount * 2  // If the bet wins, double the bet amount
            } else {
                results[bet.playerId] = 0  // If the bet loses, payout is 0
            }
        }
        return results
    }

    // Start the race and calculate payouts
    fun startRace(bets: List<Bet>, players: Map<String, Player>): Pair<HorseColor, Map<String, Int>> {
        val winner = runRace()  // Get the winning horse
        val payouts = mutableMapOf<String, Int>()

        bets.forEach { bet ->
            if (bet.horseColor == winner) {  // Compare bet's horse color with the winner
                payouts[bet.playerId] = bet.amount * 2  // Double the bet amount for winners
            }
        }
        return winner to payouts  // Return the winner and the payouts map
    }

    // Register a player
    fun registerPlayer(player: Player) {
        players[player.id] = player
    }

    // Get player details
    fun getPlayer(playerId: String): Player? {
        return players[playerId]
    }

    // Place a bet
    fun placeBet(playerId: String, horseColor: HorseColor, amount: Int): Boolean {
        val player = players[playerId] ?: return false  // If the player doesn't exist, return false
        if (player.balance >= amount) {
            player.balance -= amount  // Deduct the bet amount from the player's balance
            return true  // Bet placed successfully
        }
        return false  // Not enough balance for the bet
    }
}
