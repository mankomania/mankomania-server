package at.mankomania.server.controller

/**
 * @file PlayerControllerTest.kt
 * @author eles17
 * @since 03.05.2025
 * @description
 * Unit test class for verifying the behavior of [PlayerController].
 *
 * This test focuses on the WebSocket-based dice rolling functionality.
 * It ensures that when a player requests to roll dice:
 * - A valid DiceResult is generated.
 * - The result is sent to the correct WebSocket topic.
 * - The result contains valid die values and the correct sum.
 *
 * The class uses a mocked [SimpMessagingTemplate] to simulate message broadcasting without needing an active WebSocket server.
 */

import at.mankomania.server.util.DiceResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertEquals


class PlayerControllerTest {

    private lateinit var messagingTemplate: org.springframework.messaging.simp.SimpMessagingTemplate
    private lateinit var controller: PlayerController

    @BeforeEach
    fun setup() {
        messagingTemplate = mock()
        controller = PlayerController(messagingTemplate)
    }

    /**
     * Unit test for verifying the handleDiceRoll WebSocket method in PlayerController.
     * Ensures valid messaging behavior and result correctness.
     */
    @Test
    fun handleDiceRoll_sendsValidResultToCorrectWebSocketTopic(){
        val playerId = "blue"

        controller.handleDiceRoll(playerId)

        val topicCaptor = ArgumentCaptor.forClass(String::class.java)
        val resultCaptor = ArgumentCaptor.forClass(at.mankomania.server.util.DiceResult::class.java)

        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), resultCaptor.capture())

        val sentTopic = topicCaptor.value
        val result = resultCaptor.value

        assertEquals("/topic/diceResult/$playerId", sentTopic)
        assert(result.die1 in 1..6)
        assert(result.die2 in 1..6)
        assertEquals(result.die1 + result.die2, result.sum)
    }
}