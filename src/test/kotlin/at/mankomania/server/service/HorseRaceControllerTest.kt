package org.example.mankomaniaserverkotlin.controller

import at.mankomania.server.model.Bet
import at.mankomania.server.model.Player
import at.mankomania.server.service.HorseRaceService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/horse-race")
class HorseRaceController(private val horseRaceService: HorseRaceService) {

    // Register a player
    @PostMapping("/register")
    fun registerPlayer(@RequestBody player: Player): ResponseEntity<String> {
        horseRaceService.registerPlayer(player)
        return ResponseEntity.ok("Player registered successfully.")
    }

    // Place a bet
    @PostMapping("/place-bet")
    fun placeBet(@RequestBody bet:Bet): ResponseEntity<String> {
        val result = horseRaceService.placeBet(bet.playerId, bet.horseColor, bet.amount)
        return if (result) {
            ResponseEntity.ok("Bet placed successfully.")
        } else {
            ResponseEntity.badRequest().body("Error placing the bet.")
        }
    }

    // Wrapper class to hold both bets and players
    data class RaceRequest(
        val bets: List<Bet>,
        val players: Map<String, Player>
    )

    // Start the race and get the winner and payouts
    @PostMapping("/start")
    fun startRace(
        @RequestBody request: RaceRequest
    ): ResponseEntity<Map<String, Any>> {
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
}
