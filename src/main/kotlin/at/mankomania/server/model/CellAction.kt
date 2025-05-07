package at.mankomania.server.model

import at.mankomania.server.controller.GameController

/**
 * Represents an action that can be performed when a player lands on a board cell.
 * Subclasses should define their own behavior and provide a description.
 *
 * @property description A brief explanation of what the action does.
 */
abstract class CellAction(open val description: String) {
    /**
     * Executes the action for a player on the given game controller.
     * @param player The player who landed on the cell.
     * @param gameController The game controller managing the game state.
     */
    abstract fun execute(player: Player, gameController: GameController)
}