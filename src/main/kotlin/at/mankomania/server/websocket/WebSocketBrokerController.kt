package at.mankomania.server.websocket

import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.handler.annotation.MessageMapping
// import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
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

    /**
     * Empfängt STOMP Nachrichten, die der Client an "/app/greetings" schickt,
     * und sendet sie an alle abonnierten "/topic/greetings"
     */
    @MessageMapping("/greetings")
    @SendTo("/topic/greetings")
    fun handleGreeting(message: String): String {
        return "echo from broker: $message"
    }
}

