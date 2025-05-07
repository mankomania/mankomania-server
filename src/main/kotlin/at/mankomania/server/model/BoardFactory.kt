package at.mankomania.server.model

/**
 * @file BoardFactory.kt
 * @author Angela Drucks
 * @since 2025-05-06
 * @description Factory that creates a complete game board.
 */
object BoardFactory {

    /**
     * Creates a complete game board with a predefined number of fields and a branching rule.
     *
     * @param size The number of fields on the game board.
     * @param isBranchField A function that determines for each field whether it has a branch.
     * @return A new `Board` object.
     */
    fun createBoard(size: Int, isBranchField: (Int) -> Boolean): Board {
        val cells = List(size) { index ->
            BoardCellFactory.createBoardCell(
                index,
                hasBranch     = isBranchField(index),
                branchOptions = if (isBranchField(index))
                    listOf((index + 5) % size)  // Beispiel-Branch-Ziel
                else
                    emptyList(),
                action        = null            // später befüllen
            )
        }
        return Board(cells)
    }
}