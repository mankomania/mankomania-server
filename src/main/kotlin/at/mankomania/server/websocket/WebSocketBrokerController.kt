package at.mankomania.server.websocket

import org.springframework.messaging.handler.annotation.SendTo
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.beans.factory.annotation.Autowired



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
    fun registerName(message: String, accessor: StompHeaderAccessor) {
        val sessionId = accessor.sessionId
        println("REGISTER from session: $sessionId with name: $message")

        messagingTemplate.convertAndSend(
            "/topic/register",
            "✅ Registered: $message"
        )
    }
    fun createHeadersForSession(sessionId: String): Map<String, Any> {
        return mapOf("simpSessionId" to sessionId)
    }
}

