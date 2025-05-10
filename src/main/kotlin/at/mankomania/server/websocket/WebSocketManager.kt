package at.mankomania.server.websocket

object WebSocketManager {
    private val playerNames = mutableMapOf<String, String>()

    fun registerPlayer(sessionId: String?, name: String) {
        if (sessionId != null) {
            playerNames[sessionId] = name
            println("Player name registered: $name for session $sessionId")
        }
    }
    fun getPlayerName(sessionId: String?): String? = playerNames[sessionId]

}