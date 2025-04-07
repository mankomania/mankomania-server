package at.mankomania.server.websocket.dispatcher

import at.mankomania.server.websocket.handler.CommandHandler
import at.mankomania.server.websocket.message.GameMessage
import org.springframework.web.socket.WebSocketSession

/**
 * Klasse: WebSocketMessageDispatcher
 *
 * Zuständig für das Dispatching von eingehenden GameMessage-Objekten an den jeweils
 * registrierten CommandHandler. Jeder Handler verarbeitet Nachrichten eines bestimmten Typs.
 */
class WebSocketMessageDispatcher {
    private val commandHandlerRegistry: MutableMap<String, CommandHandler> = mutableMapOf()

    fun registerCommandHandler(
        messageType: String,
        commandHandler: CommandHandler,
    ) {
        commandHandlerRegistry[messageType] = commandHandler
    }

    fun dispatchGameMessageToHandler(
        incomingGameMessage: GameMessage,
        originatingWebSocketSession: WebSocketSession,
    ) {
        val commandHandler = commandHandlerRegistry[incomingGameMessage.type]
        if (commandHandler != null) {
            try {
                commandHandler.handleGameMessage(incomingGameMessage, originatingWebSocketSession)
            } catch (exception: Exception) {
                println("Fehler im CommandHandler für Nachrichtentyp '${incomingGameMessage.type}': ${exception.message}")
            }
        } else {
            println("Kein CommandHandler registriert für Nachrichtentyp: ${incomingGameMessage.type}")
        }
    }
}
