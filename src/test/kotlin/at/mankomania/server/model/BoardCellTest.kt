package at.mankomania.server.model

import at.mankomania.server.controller.GameController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

// Assumptions:
// - CellState, CellAction, BoardCell, and Player are defined in your production code.
//   For example:
//   enum class CellState { FREE, OCCUPIED }
//   interface CellAction { fun performAction(player: Player) }
//   data class BoardCell(val id: Int, var state: CellState = CellState.FREE, var action: CellAction? = null)
//   data class Player(val name: String)

class BoardCellTest {

    private lateinit var cellActionMock: CellAction
    private lateinit var gameControllerMock: GameController

    @BeforeEach
    fun setUp() {
        cellActionMock = Mockito.mock(CellAction::class.java)
        gameControllerMock = Mockito.mock(GameController::class.java)
    }

    @Test
    fun newBoardCellShouldHaveDefaultStateFree() {
        // Arrange: Create a new BoardCell; by default, a new cell should have state FREE
        val boardCell = BoardCell(index = 1, hasBranch = false, branchOptions = emptyList())

        // Act: Get the state of the cell.
        val actualState = boardCell.state

        // Assert: The actual state should be FREE
        assertEquals(CellState.FREE, actualState, "The default state of a new board cell should be FREE.")
    }


}