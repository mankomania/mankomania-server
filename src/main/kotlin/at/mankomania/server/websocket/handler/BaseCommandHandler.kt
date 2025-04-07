package at.mankomania.server.websocket.handler

import at.mankomania.server.websocket.message.GameMessage
import org.springframework.web.socket.WebSocketSession

fun interface BaseCommandHandler {
    fun handle(
        message: GameMessage,
        session: WebSocketSession,
    )
}
