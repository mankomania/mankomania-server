package at.mankomania.server.websocket

import org.springframework.messaging.handler.annotation.SendTo
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.beans.factory.annotation.Autowired
import at.mankomania.server.manager.GameSessionManager
import at.mankomania.server.model.MoveResult
import at.mankomania.server.controller.dto.RegisterDto


// import org.springframework.web.bind.annotation.*

// [info for apw] temporary removed in order to test stomp locally
// plan for later: take the useful lines of code from here down,
// and delete this commented part anyway
//
// @RestController
// @RequestMapping("/ws")
// class WebSocketController (
//    private val messagingTemplate: SimpMessagingTemplate
//    ) {
//    @PostMapping("/broadcast")
//    fun broadcast(@RequestBody message: String) {
//        messagingTemplate.convertAndSend("/topic/broadcast", message)
//    }
//}

@Controller
class WebSocketBrokerController {
    private val logger = LoggerFactory.getLogger(WebSocketBrokerController::class.java)

    @Autowired
    lateinit var sessionManager: GameSessionManager

    @Autowired
    lateinit var playerSocketService: PlayerSocketService

    /**
     * Empfängt STOMP Nachrichten, die der Client an "/app/greetings" schickt,
     * und sendet sie an alle abonnierten "/topic/greetings"
     */
    @MessageMapping("/greetings")
    @SendTo("/topic/greetings")
    fun handleGreeting(message: String): String {
        return "echo from broker: $message"
    }

    @Autowired
    lateinit var messagingTemplate: SimpMessagingTemplate

    @MessageMapping("/register")
    fun registerName(dto: RegisterDto, accessor: StompHeaderAccessor) {
        val sessionId = accessor.sessionId ?: return
        println("REGISTER from session: $sessionId with name: ${dto.name}, gameId: ${dto.gameId}")

        playerSocketService.registerSession(sessionId, dto.gameId)

        messagingTemplate.convertAndSend(
            "/topic/register",
            "✅ Registered: ${dto.name} in Game ${dto.gameId}"
        )
    }

    fun createHeadersForSession(sessionId: String): Map<String, Any> {
        return mapOf("simpSessionId" to sessionId)
    }

    @MessageMapping("/player-moved")
    fun handlePlayerMoved(moveResult: MoveResult, accessor: StompHeaderAccessor) {
        val sessionId = accessor.sessionId ?: return
        val gameId = playerSocketService.getGameIdForSession(sessionId) ?: return

        messagingTemplate.convertAndSend("/topic/$gameId/player-moved", moveResult)
    }

}

