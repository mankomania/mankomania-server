package at.mankomania.server.websocket.message

import kotlinx.serialization.Serializable

/**
 * Klasse: ErrorResponseMessage
 *
 * Repräsentiert eine Fehlermeldung mit einem Fehlercode, einer Beschreibung und zusätzlichen Details.
 */
@Serializable
data class ErrorResponseMessage(
    override val correlationId: String? = null,
    val errorCode: Int,
    val errorDescription: String,
    val additionalDetails: String? = null,
) : ResponseMessage()
