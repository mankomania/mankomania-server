/**
 * @file PlayerMoveService.kt
 * @author Lev Starman
 * @since 2025-06-21
 * @description Central service to compute player movement consistently across controllers.
 * Used by both REST (MoveController) and WebSocket (PlayerController) interfaces to ensure shared logic for move execution and result calculation.
 */
package at.mankomania.server.service

import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.model.MoveResult
import org.springframework.stereotype.Service

@Service
class PlayerMoveService (private val sessionManager: GameSessionManager){

    fun computeMove(gameId: String, playerId: String, steps:Int): MoveResult? {
        val controller = sessionManager.getGameController(gameId) ?: return null
        return controller.computeMoveResult(playerId, steps)
    }
}