package at.mankomania.server.controller
/**
 * @file PlayerController.kt
 * @author eles17
 * @since 25.4.2025
 * @description
 * PlayerController handles player-specific WebSocket messages, such as rolling dice.
 *
 * It listens to incoming STOMP messages (e.g., "/app/rollDice") and processes the requested action.
 * Once the dice are rolled, the result is logged, stored in the player's history, and broadcasted
 * to the appropriate WebSocket topic (e.g., "/topic/diceResult/{playerId}").
 *
 * This controller is designed for extensibility as the game adds more player-driven features,
 * such as movement, mini-games, and purchases.
 */
import at.mankomania.server.model.Player
import at.mankomania.server.util.DefaultDiceStrategy
import at.mankomania.server.util.Dice
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller


/**
 * Handles a WebSocket message for rolling dice on behalf of a specific player.
 *
 * @param playerId the unique identifier of the player (sent from the client).
 *
 * Behavior:
 * - Finds the player using the provided ID.
 * - Rolls two dice using the game's DiceStrategy.
 * - Stores the result in the player's history.
 * - Logs the rolled values to the console.
 * - Sends the result to the client subscribed to "/topic/diceResult/{playerId}".
 *
 * This method enables real-time, turn-based feedback for multiplayer gameplay.
 */
@Controller
class PlayerController(private val messagingTemplate: SimpMessagingTemplate) {

    //Uses the default strategy dor generating dice rolls
    private val dice = Dice(DefaultDiceStrategy())
    //Temporary player storage; should be replaced by proper session/player management
    private val players = mutableMapOf<String, Player>()


    //temporary mock for testing
    init{
        players["blue"] = Player("blue")
    }
    @MessageMapping("/rollDice")
    fun handleDiceRoll(@Payload playerId: String) {
        val player = players[playerId]
        if (player == null) {
            println("Player not found: $playerId")
            return
        }
        //roll dice using strategy
        val result = dice.roll()

        player.recordDiceRoll(result)

        //log
        println("Player $playerId rolled Dice: ${result.die1} + ${result.die2} = ${result.sum}")

        //send the result to the player via STOMP
        messagingTemplate.convertAndSend("/topic/diceResult/$playerId", result)

    }
}
