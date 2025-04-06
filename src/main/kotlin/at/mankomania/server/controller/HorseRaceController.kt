package at.mankomania.server.controller

import at.mankomania.server.model.Bet
import at.mankomania.server.service.HorseRaceService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/horse-race")
class HorseRaceController(
    private val horseRaceService: HorseRaceService
) {

    @PostMapping("/run")
    fun runRace(@RequestBody bets: List<Bet>): Map<String, Any> {
        val winner = horseRaceService.runRace()
        val winnings = horseRaceService.calculateWinnings(bets, winner)

        return mapOf(
            "winner" to winner,
            "winnings" to winnings
        )
    }
}
