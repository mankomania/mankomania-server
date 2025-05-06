package at.mankomania.server.model

/**
 * @file BoardCellFactory.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Factory that creates `BoardCell` instances.
 */
object BoardCellFactory {

    /**
     * Creates a new instance of `BoardCell` with the given parameters.
     *
     * @param index The index of the cell.
     * @param hasBranch Indicates whether the cell has a branch.
     * @param branchOptions The target indices of the branch, if present.
     * @param action The action to be executed on the cell.
     * @return A new `BoardCell` instance.
     */
    fun createBoardCell(index: Int, hasBranch: Boolean, branchOptions: List<Int> = emptyList(), action: CellAction? = null): BoardCell {
        return BoardCell(index, hasBranch = hasBranch, branchOptions = branchOptions, action = action)
    }
}