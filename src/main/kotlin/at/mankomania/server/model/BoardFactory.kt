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
    fun createSimpleBoard(): Board {
        val cells = mutableListOf<BoardCell>()

        val branchFieldIndices = setOf(6, 14, 21, 30)
        val startFieldIndices = setOf(0, 9, 18, 27)

        for (i in 0 until 36) {
            when {
                i in startFieldIndices -> cells.add(BoardCell(index = i, hasBranch = false))
                i in branchFieldIndices -> cells.add(
                    BoardCell(index = i, hasBranch = true, branchOptions = listOf(100 + i))
                )
                else -> cells.add(BoardCell(index = i, hasBranch = false))
            }
        }

        return Board(cells)
    }


}