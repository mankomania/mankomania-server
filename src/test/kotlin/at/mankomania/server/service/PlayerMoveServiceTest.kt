/**
 * @file PlayerMoveServiceTest.kt
 * @author Lev Starman
 * @since 2025-06-21
 * @description Unit tests for PlayerMoveService, ensuring correct computation of player moves across different edge cases.
 */
package at.mankomania.server.service

import at.mankomania.server.controller.GameController
import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.model.BoardFactory
import at.mankomania.server.model.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class PlayerMoveServiceTest {

    private lateinit var sessionManager: GameSessionManager
    private lateinit var playerMoveService: PlayerMoveService
    private lateinit var controller: GameController

    private val gameId = "test-game"
    private val playerId = "TestPlayer"

    @BeforeEach
    fun setup() {
        sessionManager = mock(GameSessionManager::class.java)
        playerMoveService = PlayerMoveService(sessionManager)

        val board = BoardFactory.createBoard(5) { false }
        val players = listOf(Player(name = playerId))
        controller = GameController(gameId, board, players, mock(BankService::class.java), mock(NotificationService::class.java))

        `when`(sessionManager.getGameController(gameId)).thenReturn(controller)
    }

    /**
     * Tests that a known player within an active game session
     * receives a valid MoveResult from the PlayerMoveService.
     */
    @Test
    fun `computeMove should return a valid result for existing player`() {
        val result = playerMoveService.computeMove(gameId, playerId, 2)
        assertNotNull(result)
        assertEquals(playerId, controller.getPlayer(playerId)?.name)
    }

    /**
     * Tests that PlayerMoveService returns null for a non-existent player.
     */
    @Test
    fun `computeMove should return null for invalid player`() {
        val result = playerMoveService.computeMove(gameId, "Unknown", 2)
        assertNull(result)
    }

    /**
     * Tests that PlayerMoveService returns null when no GameController is found for the gameId.
     */
    @Test
    fun `computeMove should return null if no controller is found`() {
        `when`(sessionManager.getGameController("invalid")).thenReturn(null)
        val result = playerMoveService.computeMove("invalid", playerId, 2)
        assertNull(result)
    }
}