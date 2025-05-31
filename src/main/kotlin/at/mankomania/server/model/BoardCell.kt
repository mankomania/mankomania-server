/**
 * @file BoardCell.kt
 * @author eles17, Angela Drucks,
 * @since 2025-04-02
 * @description Repräsentiert ein einzelnes Spielfeld (Cell) inkl. Position und zugewiesener Action.
 */
package at.mankomania.server.model

import at.mankomania.server.controller.GameController

/**
 * @property index The position of this field on the board (0-based).
 * @property state The current state of the cell (FREE, OCCUPIED, etc.).
 * @property hasBranch Indicates whether the field allows the player to choose a different path (branch).
 * @property branchOptions Possible branch destination indices.
 * @property action The action to be executed when the player lands on the cell.
 */

data class BoardCell(
    val index:Int,
    var state: CellState = CellState.FREE,
    val hasBranch: Boolean,
    val branchOptions: List<Int> = emptyList(),
    var action: CellAction? = null,
    val type: String? = null
) {
    /**
     * Handles a player landing on the cell.
     * Executes the cell's action (if present) and then marks the cell as OCCUPIED.
     */
    fun landOn(player: Player, gameController: GameController) {
        action?.execute(player, gameController)
        state = CellState.OCCUPIED
    }


}
