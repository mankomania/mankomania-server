/**
 * @file BoardCellTest.kt
 * @author Angela Drucks
 * @since 2025-04-07
 * @description Unit-Testklasse zur Überprüfung der Spielfeldlogik in [BoardCell], inklusive Konstruktor-Tests,
 *              Branch-Logik und Modulo-basiertem Feldzugriff.
 */

package at.mankomania.server.model

import at.mankomania.server.controller.GameController
import org.junit.jupiter.api.Assertions.assertNull
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

    @Test
    fun landOnShouldUpdateStateRegardlessOfBranch() {
        // Arrange: Create a branching cell
        val branchCell = BoardCell(
            index = 10,
            hasBranch = true,
            branchOptions = listOf(20, 25),
            action = null
        )
        val player = Player("TestPlayer")

        // Act
        branchCell.landOn(player, gameControllerMock)

        // Assert: Even though it's a branching cell, once the player lands, the state should become OCCUPIED
        assertEquals(CellState.OCCUPIED, branchCell.state)
    }

    @Test
    fun setStateShouldChangeCellState() {
        val boardCell = BoardCell(
            index = 4,
            hasBranch = false,
            branchOptions = emptyList(),
            action = null
        )

        // Act: Change the state
        boardCell.state = CellState.OCCUPIED

        // Assert: The state should be updated
        assertEquals(CellState.OCCUPIED, boardCell.state,
            "Setting the state to OCCUPIED should update the BoardCell's state correctly.")
    }

    @Test
    fun setStateShouldUpdateCellState() {
        val boardCell = BoardCell(
            index = 10,
            hasBranch = false
        )

        // Initially FREE
        assertEquals(CellState.FREE, boardCell.state, "Initial cell state should be FREE.")

        // Act: Set the state to OCCUPIED
        boardCell.state = CellState.OCCUPIED

        // Assert: The cell state should now be OCCUPIED
        assertEquals(CellState.OCCUPIED, boardCell.state, "Cell state should be updated to OCCUPIED.")
    }

    @Test
    fun setActionShouldChangeCellAction() {
        val boardCell = BoardCell(
            index = 5,
            hasBranch = false,
            branchOptions = emptyList(),
            action = null
        )

        // Initially, action is null
        assertNull(boardCell.action, "By default, the action should be null if not provided in the constructor.")

        // Act: Assign a mock action
        boardCell.action = cellActionMock

        // Assert: The action should be updated
        assertEquals(cellActionMock, boardCell.action,
            "Setting the action should update the BoardCell's action.")
    }


    @Test
    fun getActionShouldReturnCurrentAction() {
        // Arrange: Create a BoardCell with a mock action in the constructor
        val boardCell = BoardCell(
            index = 12,
            hasBranch = false,
            action = cellActionMock
        )

        // Act: Retrieve the current action
        val currentAction = boardCell.action

        // Assert: Confirm it matches the mock
        assertEquals(cellActionMock, currentAction, "Getter should return the currently assigned action.")
    }


}