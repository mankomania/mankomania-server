package at.mankomania.server.websocket.message

import kotlinx.serialization.Serializable

/**
 * Abstrakte Klasse: ResponseMessage
 *
 * Basis-Klasse für Antwortnachrichten, die über den WebSocket zurückgesendet werden.
 */
@Serializable
sealed class ResponseMessage {
    abstract val correlationId: String?
}
