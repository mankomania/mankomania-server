package at.mankomania.server.websocket

import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ws")
class WebSocketController (
    private val messagingTemplate: SimpMessagingTemplate
    ) {
    @PostMapping("/broadcast")
    fun broadcast(@RequestBody message: String) {
        messagingTemplate.convertAndSend("/topic/broadcast", message)
    }
}
