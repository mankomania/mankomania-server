package at.mankomania.server.websocket.handler

import at.mankomania.server.websocket.message.GameMessage
import org.springframework.web.socket.WebSocketSession

/**
 * Interface: CommandHandler
 *
 * Definiert die Funktionalität zum Verarbeiten von eingehenden GameMessage-Objekten,
 * die über einen WebSocket empfangen wurden.
 */
fun interface CommandHandler {
    fun handleGameMessage(
        incomingGameMessage: GameMessage,
        originatingWebSocketSession: WebSocketSession,
    )
}
