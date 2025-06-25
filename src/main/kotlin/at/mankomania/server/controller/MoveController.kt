package at.mankomania.server.controller

import at.mankomania.server.controller.dto.MoveRequest
import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.model.MoveResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import at.mankomania.server.controller.dto.DiceMoveResultDto
import at.mankomania.server.service.NotificationService

/**
 * @file MoveController.kt
 * @author Lev
 * @since 2025-05-07
 * @description
 * Provides REST endpoints for movement-related game actions such as player movement requests.
 */

@RestController
@RequestMapping("/move")
class MoveController(
private val sessionManager: GameSessionManager
) {
    /**
     * Handles a player move request for a specific game instance.
     * Delegates to GameController's computeMoveResult method and returns the result.
     *
     * @param gameId The ID of the game session.
     * @param request The move request containing player ID and number of steps.
     * @return A MoveResult object or 400 Bad Request if the game or player is invalid.
     */

    @PostMapping("/{gameId}")
    fun move(
        @PathVariable gameId: String,
        @RequestBody request: MoveRequest
    ): ResponseEntity<MoveResult> {
        val controller = sessionManager.getGameController(gameId) ?: return ResponseEntity.badRequest().build()
        val result = controller.computeMoveResult(gameId, request.playerId, request.steps)
            ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(result)
    }
    @RestController
    @RequestMapping("/game")
    class GameMoveController(
        private val notificationService: NotificationService
    ) {
        @PostMapping("/move")
        fun handleDiceMove(@RequestBody result: DiceMoveResultDto) {
            notificationService.sendToGameTopic(result.playerId, "/topic/game/move", result)
        }
    }


}