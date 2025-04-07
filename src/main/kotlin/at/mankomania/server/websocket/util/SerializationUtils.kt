package at.mankomania.server.websocket.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Objekt: SerializationUtils
 *
 * Bietet Hilfsfunktionen zum Serialisieren und Deserialisieren von Nachrichten in bzw. aus JSON.
 */
object SerializationUtils {
    val json: Json = Json { ignoreUnknownKeys = true }

    inline fun <reified T> deserializeJsonToMessage(jsonString: String): T = json.decodeFromString(jsonString)

    inline fun <reified T> serializeMessageToJson(messageObject: T): String = json.encodeToString(messageObject)
}
