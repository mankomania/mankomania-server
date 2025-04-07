package at.mankomania.server.controller

import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import at.mankomania.server.model.Player
import at.mankomania.server.service.HorseRaceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/horse-race")
class HorseRaceController(private val horseRaceService: HorseRaceService) {

    // Start the race and get the winner and payouts
    @PostMapping("/start")
    fun startRace(@RequestBody request: RaceRequest): ResponseEntity<Map<String, Any>> {
        val (winner, payouts) = horseRaceService.startRace(request.bets, request.players)
        return ResponseEntity.ok(
            mapOf(
                "winner" to winner.name,
                "payouts" to payouts
            )
        )
    }

    @GetMapping("/player/{id}")
    fun getPlayer(@PathVariable id: String): ResponseEntity<Player?> {
        val player = horseRaceService.getPlayer(id)
        return if (player != null) {
            ResponseEntity.ok(player)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    data class RaceRequest(
        val bets: List<Bet>,
        val players: Map<String, Player>
    )
}
