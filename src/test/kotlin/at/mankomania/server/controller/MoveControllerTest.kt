package at.mankomania.server.controller

import at.mankomania.server.controller.dto.MoveRequest
import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.model.MoveResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.ResponseEntity

/**
 * @file MoveControllerTest.kt
 * @author Lev
 * @since 2025-05-07
 * @description
 * Unit tests for MoveController class to ensure correct handling of movement requests.
 */

class MoveControllerTest {

    private lateinit var sessionManager: GameSessionManager
    private lateinit var controller: MoveController

    @BeforeEach
    fun setup() {
        sessionManager = Mockito.mock(GameSessionManager::class.java)
        controller = MoveController(sessionManager)
    }

    /**
     * Test case: Valid move request.
     * Verifies that when both the game controller and player are valid,
     * the controller returns HTTP 200 and the expected MoveResult object.
     */
    @Test
    fun `move should return MoveResult when controller and player are valid`() {
        // Arrange: Create a mock GameController and MoveResult
        val mockGameController = Mockito.mock(GameController::class.java)
        val moveResult = MoveResult(2, 0, "PayFeeAction", "Pay fee to bank", listOf("Player2"))

        // Simulate GameSessionManager returning the mock controller and the controller returning the MoveResult
        Mockito.`when`(sessionManager.getGameController("game123")).thenReturn(mockGameController)
        Mockito.`when`(mockGameController.computeMoveResult("Player1", "2", 2)).thenReturn(moveResult)

        // Act: Send a valid move request
        val request = MoveRequest("Player1", 2)
        val response: ResponseEntity<MoveResult> = controller.move("game123", request)

        // Assert: The response is HTTP 200 and matches the expected MoveResult
        assertEquals(200, response.statusCode.value())
        assertEquals(moveResult, response.body)
    }

    /**
     * Test case: Game controller not found.
     * Verifies that when the GameSessionManager returns null for an invalid game ID,
     * the controller returns HTTP 400 and no body.
     */
    @Test
    fun `move should return 400 when game controller is not found`() {
        // Arrange: Simulate GameSessionManager returning null for an invalid game ID
        Mockito.`when`(sessionManager.getGameController("invalidGame")).thenReturn(null)

        // Act: Send a move request with an invalid game ID
        val request = MoveRequest("Player1", 2)
        val response: ResponseEntity<MoveResult> = controller.move("invalidGame", request)

        // Assert: The response is HTTP 400 and no body is returned
        assertEquals(400, response.statusCode.value())
        assertNull(response.body)
    }

    /**
     * Test case: Player not found in the game.
     * Verifies that when the GameController returns null for a non-existent player,
     * the controller returns HTTP 400 and no body.
     */
    @Test
    fun `move should return 400 when player is not found`() {
        // Arrange: Simulate GameController returning null for a non-existent player
        val mockGameController = Mockito.mock(GameController::class.java)

        Mockito.`when`(sessionManager.getGameController("game123")).thenReturn(mockGameController)
        Mockito.`when`(mockGameController.computeMoveResult("Ghost", "4",2)).thenReturn(null)

        // Act: Send a move request with a non-existent player
        val request = MoveRequest("Ghost", 4)
        val response: ResponseEntity<MoveResult> = controller.move("game123", request)

        // Assert: The response is HTTP 400 and no body is returned
        assertEquals(400, response.statusCode.value())
        assertNull(response.body)
    }
}