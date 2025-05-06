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
import at.mankomania.server.model.Player
import at.mankomania.server.service.BankService
import at.mankomania.server.service.NotificationService


class GameController(
    private val board: Board,
    private val players: List<Player>,
    private val bankService: BankService = BankService(),
    private val notificationService: NotificationService = NotificationService()
) {
    fun startGame() {
        // notificationService.sendGameState(players)
    }

    fun movePlayer(playerId: String, steps: Int) {
        // val player = players.find { it.name == playerId } ?: return
        //val branched = player.move(steps, board)
        //if (!branched) landOnCell(playerId, player.position)
        //notificationService.sendPlayerMoved(playerId, player.position)

    }

    fun landOnCell(playerId: String, cellIndex: Int) {
        // val player = players.find { it.name == playerId } ?: return
        //board.getCell(cellIndex).landOn(player, this)
        //notificationService.sendPlayerLanded(playerId, cellIndex)
    }
}