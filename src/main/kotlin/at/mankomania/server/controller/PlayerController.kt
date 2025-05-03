package at.mankomania.server.controller
/**
 * @file PlayerController.kt
 * @author eles17
 * @since 25.4.2025
 * @description
 *  PlayerController is responsible for handling player-specific actions such as rolling dice.
 *  It listens to WebSocket messages and sends back real-time results using messaging topics.
 *  The controller is designed for extensibility as more player actions (e.g., movement, purchases) are added.
 */
import at.mankomania.server.model.Player
import at.mankomania.server.util.DefaultDiceStrategy
import at.mankomania.server.util.Dice
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller


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
