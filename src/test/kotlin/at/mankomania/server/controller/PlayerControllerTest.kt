package at.mankomania.server.controller

/**
 * @file PlayerControllerTest.kt
 * @author eles17
 * @since 11.5.2025
 * @description
 * Unit tests for PlayerController's handleDiceRoll method, verifying correct WebSocket messaging behavior.
 */

import at.mankomania.server.controller.dto.DiceMoveResultDto
import at.mankomania.server.controller.GameController
import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.model.MoveResult
import at.mankomania.server.util.Dice
import at.mankomania.server.util.DiceResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.springframework.messaging.simp.SimpMessagingTemplate

class PlayerControllerTest {

    private lateinit var messagingTemplate: SimpMessagingTemplate
    private lateinit var sessionManager: GameSessionManager
    private lateinit var controller: PlayerController

    /**
     * Initializes mocks and the PlayerController before each test.
     */
    @BeforeEach
    fun setUp() {
        messagingTemplate = mock(SimpMessagingTemplate::class.java)
        sessionManager = mock(GameSessionManager::class.java)
        controller = PlayerController(messagingTemplate, sessionManager)
    }

    /**
     * Happy path: when computeMoveResult returns non-null, a DiceMoveResultDto should be sent to the correct topic.
     */
    @Test
    fun `handleDiceRoll sends correct result when moveResult is non-null`() {
        // Arrange
        val playerId = "player1"
        val diceResult = DiceResult(die1 = 2, die2 = 3)
        val expectedResult = MoveResult(
            newPosition = 10,
            oldPosition = 7,
            fieldType = "NORMAL",
            fieldDescription = "Sample Field",
            playersOnField = listOf(playerId)
        )
        val gameController = mock(GameController::class.java)
        `when`(sessionManager.getGameController("default")).thenReturn(gameController)
        `when`(gameController.computeMoveResult(playerId, diceResult.sum)).thenReturn(expectedResult)

        // Inject deterministic dice
        val diceField = PlayerController::class.java.getDeclaredField("dice")
        diceField.isAccessible = true
        diceField.set(controller, Dice { diceResult })

        // Act
        controller.handleDiceRoll("default", playerId)

        // Assert
        val topicCaptor = ArgumentCaptor.forClass(String::class.java)
        val dtoCaptor = ArgumentCaptor.forClass(DiceMoveResultDto::class.java)
        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), dtoCaptor.capture())

        assertEquals("/topic/diceResult/$playerId", topicCaptor.value)
        val dto = dtoCaptor.value
        assertEquals(playerId, dto.playerId)
        assertEquals(2, dto.die1)
        assertEquals(3, dto.die2)
        assertEquals(5, dto.sum)
        assertEquals(expectedResult.newPosition, dto.fieldIndex)
        assertEquals(expectedResult.fieldType, dto.fieldType)
        assertEquals(expectedResult.fieldDescription, dto.fieldDescription)
        assertEquals(expectedResult.playersOnField, dto.playersOnField)
    }

    /**
     * When no GameController is found for the default game, no message should be sent.
     */
    @Test
    fun `handleDiceRoll returns early when gameController is null`() {
        // Arrange
        `when`(sessionManager.getGameController("default")).thenReturn(null)

        // Act
        controller.handleDiceRoll("default", "player1")

        // Assert: no messages at all
        verifyNoInteractions(messagingTemplate)
    }
    /**
     * When computeMoveResult returns null (move failed), no message should be sent.
     */
    @Test
    fun `handleDiceRoll returns early when moveResult is null`() {
        // Arrange
        val playerId = "player1"
        val diceResult = DiceResult(die1 = 4, die2 = 5)
        val gameController = mock(GameController::class.java)
        `when`(sessionManager.getGameController("default")).thenReturn(gameController)
        `when`(gameController.computeMoveResult(playerId, diceResult.sum)).thenReturn(null)

        // Inject deterministic dice
        val diceField = PlayerController::class.java.getDeclaredField("dice")
        diceField.isAccessible = true
        diceField.set(controller, Dice { diceResult })

        // Act
        controller.handleDiceRoll("default", playerId)

        // Assert: no messages at all
        verifyNoInteractions(messagingTemplate)
    }
}