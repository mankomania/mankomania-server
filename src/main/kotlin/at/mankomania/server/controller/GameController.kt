/**
 * @file GameController.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Verwaltet die Spiellogik für das Mankomania-Spiel,
 * einschließlich der Steuerung der Spielerbewegung und der Ausführung
 * von Zellenaktionen auf dem Spielfeld.
 */

package at.mankomania.server.controller

import at.mankomania.server.model.Board


class GameController(val board: Board) {
    fun startGame() {
        // Spiel starten, Spieler platzieren, etc
    }

    fun movePlayer(playerId: String, steps: Int) {
        // Spieler bewegen
    }

    fun landOnCell(playerId: String, cellIndex: Int) {
        // Aktion ausführen, wenn Spieler auf einer Zelle landet
    }
}