package at.mankomania.server.controller

import at.mankomania.server.controller.dto.DiceMoveResultDto
import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.util.DefaultDiceStrategy
import at.mankomania.server.util.Dice
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller


/**
 * @file PlayerController.kt
 * @author eles17
 * @since 25.04.2025
 * @description
 * PlayerController handles WebSocket-based actions triggered by players, such as rolling dice.
 * When a die roll is received from the client, it applies movement logic and sends back
 * a combined result (dice + field) via STOMP to the client.
 *
 * This controller delegates all gameplay logic to GameSessionManager and GameController.
 */
@Controller
class PlayerController(
    private val messagingTemplate: SimpMessagingTemplate,
    private val sessionManager: GameSessionManager
) {
    // Dice roller using the default (random) strategy
    private val dice = Dice(DefaultDiceStrategy())

    companion object {
        private val log = LoggerFactory.getLogger(PlayerController::class.java)
    }

    /**
     * Handles incoming WebSocket dice roll messages from the frontend.
     *
     * @param playerId the identifier of the player who initiated the dice roll
     */
    @MessageMapping("/rollDice/{gameId}")
    fun handleDiceRoll(@DestinationVariable gameId: String, @Payload playerId: String) {

        val controller = sessionManager.getGameController(gameId)
        if (controller == null) {
            log.warn("Game not found for gameId: {}", gameId)
            return
        }

        // Roll the dice
        val diceResult = dice.roll()

        // Store the dice result in the player's history for game state tracking
        controller.getPlayer(playerId)?.recordDiceRoll(diceResult)

        // Apply movement logic and get resulting field data
        val moveResult = controller.computeMoveResult(playerId, diceResult.sum)
        if (moveResult == null) {
            log.warn("Move failed for player: {}", playerId)
            return
        }

        // Create response DTO containing dice result + move result
        val dto = DiceMoveResultDto(
            playerId = playerId,
            die1 = diceResult.die1,
            die2 = diceResult.die2,
            sum = diceResult.sum,
            fieldIndex = moveResult.newPosition,
            fieldType = moveResult.fieldType,
            fieldDescription = moveResult.fieldDescription,
            playersOnField = moveResult.playersOnField
        )

        // Send to subscribed frontend clients
        messagingTemplate.convertAndSend("/topic/diceResult/$playerId", dto)
    }
}
