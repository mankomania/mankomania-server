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

        val startFieldIndices = setOf(0, 12, 20, 32)
        val branchFieldMap = mapOf(
            4 to listOf(6, 7, 8, 9),    // → M1
            14 to listOf(15, 16, 17, 18),   // → M2
            24 to listOf(25, 26, 27, 28),   // → M3
            34 to listOf(35,36,37,38)    // → M4
        )
        val lotteryIndex = 3

        for (i in 0 until 40) {
            when {
                i == lotteryIndex -> cells.add(BoardCell(index = i, hasBranch = false, type = "LOTTERY"))
                i in startFieldIndices -> cells.add(BoardCell(index = i, hasBranch = false, type = "START"))
                i in branchFieldMap.keys -> cells.add(BoardCell(index = i, hasBranch = true, branchOptions = branchFieldMap[i] ?: emptyList(), type = "BRANCH"))
                else -> cells.add(BoardCell(index = i, hasBranch = false, type = "NORMAL"))
            }
        }

        var currentIndex = 40
        for (branch in branchFieldMap.values) {
            for (target in branch) {
                cells.add(BoardCell(index = currentIndex++, hasBranch = false))
            }
        }

        return Board(cells)
    }




}