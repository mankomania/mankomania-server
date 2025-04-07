package at.mankomania.server.websocket.config

import at.mankomania.server.websocket.GameWebSocketSessionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Klasse: WebSocketConfiguration
 *
 * Konfiguriert den WebSocket-Endpunkt und registriert den GameWebSocketSessionHandler für
 * den Umgang mit WebSocket-Verbindungen.
 */
@Configuration
@EnableWebSocket
class WebSocketConfiguration : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(GameWebSocketSessionHandler(), "/ws")
            .setAllowedOrigins("*")
    }
}
