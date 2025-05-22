package at.mankomania.server.model.actions

import at.mankomania.server.controller.GameController
import at.mankomania.server.model.CellAction
import at.mankomania.server.model.Player

class MinigamePlaceholderAction : CellAction() {
    override val description = "Minigame in progress – coming soon!"
    override fun execute(player: Player, controller: GameController) {
        println("Minigame placeholder triggered (index=${player.position})")
    }
}
