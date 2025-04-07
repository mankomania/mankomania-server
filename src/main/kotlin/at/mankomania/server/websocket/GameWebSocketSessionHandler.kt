package at.mankomania.server.websocket

import at.mankomania.server.websocket.util.SerializationUtils
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

/**
 * Klasse: GameWebSocketSessionHandler
 *
 * Verantwortlich für das Verwalten der WebSocket-Verbindungen und -Nachrichten.
 * Hier erfolgt auch die exklusive Sitzungsverwaltung inklusive Zählung der aktiven Verbindungen.
 */
class GameWebSocketSessionHandler : TextWebSocketHandler() {
    private val maximumAllowedPlayers: Int = 7
    private val activeWebSocketSessionSet: MutableSet<WebSocketSession> = mutableSetOf()

    // Verarbeitung eingehender Textnachrichten.
    override fun handleTextMessage(
        session: WebSocketSession,
        textMessage: TextMessage,
    ) {
        val incomingMessagePayload = textMessage.payload
        try {
            // Überprüft die Gültigkeit des JSON-Formats.
            SerializationUtils.json.parseToJsonElement(incomingMessagePayload)
            println("Erfolgreich verarbeitete gültige Nachricht: $incomingMessagePayload")
        } catch (exception: Exception) {
            val errorResponseJson =
                """{"errorCode":400,"errorMessage":"Fehler bei der 
                |Nachrichtenverarbeitung","details":"$incomingMessagePayload"}
                """.trimMargin()
            session.sendMessage(TextMessage(errorResponseJson))
            println("Fehlerantwort an Session ${session.id} gesendet: $errorResponseJson")
        }
    }

    // Wird aufgerufen, sobald eine neue WebSocket-Verbindung aufgebaut wurde.
    override fun afterConnectionEstablished(session: WebSocketSession) {
        if (activeWebSocketSessionSet.size >= maximumAllowedPlayers) {
            session.close(CloseStatus(4001, "Raum ist voll"))
            println("Verbindung abgelehnt – Raum voll (Session ID: ${session.id})")
            return
        }
        activeWebSocketSessionSet.add(session)
        updateActiveSessionCountDisplay()
        println("Session registriert: ${session.id}")
    }

    // Wird aufgerufen, wenn eine WebSocket-Verbindung geschlossen wird.
    override fun afterConnectionClosed(
        session: WebSocketSession,
        closeStatus: CloseStatus,
    ) {
        activeWebSocketSessionSet.remove(session)
        updateActiveSessionCountDisplay()
        println("Session abgemeldet: ${session.id}")
    }

    // Zeigt die aktuelle Anzahl aktiver Sessions an.
    private fun updateActiveSessionCountDisplay() {
        println("Aktive WebSocket-Sitzungen: ${activeWebSocketSessionSet.size}")
    }

    // Liefert eine unveränderliche Kopie der aktiven Sitzungen, die für Broadcast-Zwecke genutzt werden kann.
    fun getActiveWebSocketSessions(): Set<WebSocketSession> = activeWebSocketSessionSet.toSet()
}
