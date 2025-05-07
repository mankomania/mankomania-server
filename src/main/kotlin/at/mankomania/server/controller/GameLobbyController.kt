/*
 * src/main/kotlin/at/mankomania/server/controller/GameLobbyController.kt
 */
package at.mankomania.server.controller

import at.mankomania.server.controller.dto.JoinDto
import at.mankomania.server.controller.dto.LobbyStateDto
import at.mankomania.server.controller.dto.StartDto
import at.mankomania.server.controller.dto.GameStartedDto
/**
 * @file GameLobbyController.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Provides REST endpoints for lobby management:
 * allowing players to join a game lobby
 * and to start a game session once minimum 2 players have joined.
 */

import at.mankomania.server.manager.GameSessionManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/lobby")
class GameLobbyController(
    private val sessionManager: GameSessionManager
) {
    @PostMapping("/{gameId}/join")
    fun join(
        @PathVariable gameId: String,
        @RequestBody dto: JoinDto
    ): ResponseEntity<LobbyStateDto> {
        val added = sessionManager.joinGame(gameId, dto.playerName)
        val playerNames = sessionManager.getPlayers(gameId).map { it.name }
        val canStart = playerNames.size in 2..4
        val state = LobbyStateDto(gameId, playerNames, canStart)
        return if (added) ResponseEntity.ok(state)
        else ResponseEntity.badRequest().body(state)
    }

    @PostMapping("/{gameId}/start")
    fun start(
        @PathVariable gameId: String,
        @RequestBody dto: StartDto
    ): ResponseEntity<GameStartedDto> {
        val startPositions = sessionManager.startSession(gameId, dto.boardSize)
            ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(GameStartedDto(gameId, startPositions))
    }
}