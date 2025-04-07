package at.mankomania.server.websocket.dispatcher

import at.mankomania.server.websocket.handler.BaseCommandHandler
import at.mankomania.server.websocket.message.GameMessage
import org.springframework.web.socket.WebSocketSession

class MessageDispatcher {
    private val handlers: MutableMap<String, BaseCommandHandler> = mutableMapOf()

    fun registerHandler(
        type: String,
        handler: BaseCommandHandler,
    ) {
        handlers[type] = handler
    }

    fun dispatch(
        message: GameMessage,
        session: WebSocketSession,
    ) {
        val handler = handlers[message.type]
        if (handler != null) {
            try {
                handler.handle(message, session)
            } catch (e: Exception) {
                // Catch and log the exception instead of propagating it.
                println("Exception [ERROR] in handler for message type '${message.type}': ${e.message}")
            }
        } else {
            println("No handler [ERROR] for message type: ${message.type}")
        }
    }
}
