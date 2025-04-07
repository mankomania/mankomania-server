package at.mankomania.server.websocket

import org.springframework.web.socket.WebSocketSession

/**
 * Klasse: WebSocketSessionManager
 *
 * Verwaltet WebSocket-Sitzungen in einer Map, wobei die Verwaltung (Zählung, Hinzufügen, Entfernen)
 * primär im GameWebSocketSessionHandler erfolgt. Diese Klasse dient als ergänzendes Repository, falls
 * eine Zuordnung von Session-IDs zu Sitzungen benötigt wird.
 */
object WebSocketSessionManager {
    private val webSocketSessionMap: MutableMap<String, WebSocketSession> = mutableMapOf()

    fun addWebSocketSession(newWebSocketSession: WebSocketSession) {
        webSocketSessionMap[newWebSocketSession.id] = newWebSocketSession
    }

    fun removeWebSocketSession(existingWebSocketSession: WebSocketSession) {
        webSocketSessionMap.remove(existingWebSocketSession.id)
    }

    fun getWebSocketSessionById(sessionId: String): WebSocketSession? = webSocketSessionMap[sessionId]

    fun clearAllWebSocketSessions() {
        webSocketSessionMap.clear()
    }

    // Hinweis: Die tatsächliche Zählung der aktiven Sitzungen erfolgt im GameWebSocketSessionHandler.
    fun getRegisteredWebSocketSessionCount(): Int = webSocketSessionMap.size
}
