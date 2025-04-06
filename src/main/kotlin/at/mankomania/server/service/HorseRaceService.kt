package at.mankomania.server.service

import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import org.springframework.stereotype.Service

@Service
class HorseRaceService {

    /**
     * Simulates a horse race and determines the winner randomly.
     */
    fun runRace(): HorseColor {
        return HorseColor.values().random()
    }

    /**
     * Calculates the winnings for each player based on their bet and the winning horse.
     */
    fun calculateWinnings(bets: List<Bet>, winningHorse: HorseColor): Map<String, Int> {
        val result = mutableMapOf<String, Int>()

        for (bet in bets) {
            if (bet.horseColor == winningHorse) {
                result[bet.playerId] = bet.amount * 2
            } else {
                result[bet.playerId] = 0
            }
        }

        return result
    }
}
