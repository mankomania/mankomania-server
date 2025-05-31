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

        val startFieldIndices = setOf(0, 8, 13, 20)
        val branchFieldMap = mapOf(
            5 to listOf(24, 25),    // → M1
            10 to listOf(26, 27),   // → M2
            16 to listOf(28, 29),   // → M3
            22 to listOf(30, 31)    // → M4
        )
        val lotteryIndex = 3
        val minigameIndices = setOf(25, 27, 29, 31)

        for (i in 0 until 36) {
            when {
                i in startFieldIndices -> cells.add(BoardCell(index = i, hasBranch = false))
                i == lotteryIndex -> {
                    cells.add(BoardCell(index = i, hasBranch = false, isLottery = true))
                }

                i in branchFieldMap.keys -> {
                    cells.add(
                        BoardCell(index = i, hasBranch = true, branchOptions = branchFieldMap[i]!!)
                    )
                }
                else -> cells.add(BoardCell(index = i, hasBranch = false))
            }
        }
        minigameIndices.forEach { i ->
            cells.add(BoardCell(index = i, hasBranch = false, isMinigame = true))
        }


        return Board(cells)
    }


}