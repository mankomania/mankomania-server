package at.mankomania.server.websocket.message

import kotlinx.serialization.Serializable

/**
 * Abstrakte Klasse: GameMessage
 *
 * Basis-Klasse für alle spielbezogenen Nachrichten, die über den WebSocket ausgetauscht werden.
 */
@Serializable
abstract class GameMessage(
    val type: String,
    open val correlationId: String? = null,
)
