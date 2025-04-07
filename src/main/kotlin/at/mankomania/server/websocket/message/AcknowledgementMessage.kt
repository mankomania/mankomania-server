package at.mankomania.server.websocket.message

import kotlinx.serialization.Serializable

/**
 * Klasse: AcknowledgementMessage
 *
 * Stellt eine erfolgreiche Bestätigungsnachricht dar, die als Antwort über den WebSocket versendet wird.
 */
@Serializable
data class AcknowledgementMessage(
    override val correlationId: String? = null,
    val status: String = "success",
    val messageDetail: String? = null,
) : ResponseMessage()
