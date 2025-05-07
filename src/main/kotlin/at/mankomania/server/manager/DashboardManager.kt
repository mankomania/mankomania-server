/**
 * @file DashboardManager.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Verantwortlich für das Verwalten und Anzeigen von Spielinformationen und Statistiken, wie etwa der aktiven Spiele und des Spielstands.
 */

package at.mankomania.server.manager

class DashboardManager {
    fun getActiveGames(): List<String> {
        // Gibt eine Liste der aktiven Spiele zurück
        return listOf("Game1", "Game2")
    }

    fun getPlayerStatus(gameId: String): String {
        // Gibt den Status eines Spielers im Spiel zurück
        return "Player 1: On cell 5"
    }
}