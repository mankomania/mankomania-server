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