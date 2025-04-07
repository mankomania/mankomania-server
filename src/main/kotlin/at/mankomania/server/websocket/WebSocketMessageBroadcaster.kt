package at.mankomania.server.websocket

import at.mankomania.server.websocket.util.SerializationUtils
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

/**
 * Klasse: WebSocketMessageBroadcaster
 *
 * Verantwortlich für den reinen Nachrichtenversand (Broadcast) an eine übergebene Sammlung
 * von aktiven WebSocket-Sitzungen. Es erfolgt keinerlei Sitzungsverwaltung in dieser Klasse.
 */
object WebSocketMessageBroadcaster {
    inline fun <reified T> broadcastMessageToSessions(
        messageToBroadcast: T,
        activeWebSocketSessions: Collection<WebSocketSession>,
    ) {
        val serializedJsonMessage = SerializationUtils.serializeMessageToJson(messageToBroadcast)
        activeWebSocketSessions.forEach { session ->
            try {
                session.sendMessage(TextMessage(serializedJsonMessage))
            } catch (exception: Exception) {
                println("Fehler beim Senden der Nachricht an Session ${session.id}: ${exception.message}")
            }
        }
        println("Broadcast-Nachricht gesendet: $serializedJsonMessage")
    }
}
