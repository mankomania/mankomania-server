package at.mankomania.server.model

import at.mankomania.server.controller.GameController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

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

    @Test
    fun landOnShouldInvokeActionAndSetStateToOccupied() {
        // Arrange: Create a BoardCell with a mock action
        val boardCell = BoardCell(
            index = 2,
            hasBranch = false,
            branchOptions = emptyList(),
            action = cellActionMock
        )
        val testPlayer = Player("TestPlayer")

        // Act: landOn is called
        boardCell.landOn(testPlayer, gameControllerMock)

        // Assert: Verify that execute(...) was called and the state is now OCCUPIED
        Mockito.verify(cellActionMock, Mockito.times(1)).execute(testPlayer, gameControllerMock)
        assertEquals(CellState.OCCUPIED, boardCell.state, "BoardCell should transition to OCCUPIED after landOn.")
    }


}