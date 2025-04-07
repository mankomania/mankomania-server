package at.mankomania.server.model

import at.mankomania.server.controller.GameController

/**
 * @file CellAction.kt
 * @author Angela Drucks
 * @since 2025-04-02
 * @description Represents an action that can be performed when a player lands on a board cell.
 */

interface CellAction {

    /**
     * Executes the action for a player on the given game controller.
     * @param player The player who landed on the cell.
     * @param gameController The game controller managing the game state.
     */
    fun execute(player: Player, gameController: GameController)
}