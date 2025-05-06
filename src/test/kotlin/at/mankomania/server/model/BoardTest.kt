/**
 * @file BoardTest.kt
 * @author Angela Drucks
 * @since 2025-04-07
 * @description Unit-Testklasse zur Überprüfung der Spielfeldlogik in Board.kt, inklusive Konstruktor-Tests,
 *              Branch-Logik und Modulo-basiertem Feldzugriff.
 */

package at.mankomania.server.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class BoardTest {

    private lateinit var defaultBoard: Board
    private lateinit var customBoard: Board

    @BeforeEach
    fun setUp() {
        // Constructor 1: Board of size 10, cells 3 and 7 are branch cells
        defaultBoard = Board(10) { index -> index == 3 || index == 7 }

        // Constructor 2: Manually constructed cells with specific branchOptions
        val cells = List(5) { index ->
            BoardCell(
                index = index,
                hasBranch = index % 2 == 0,
                branchOptions = if (index % 2 == 0) listOf(index + 5) else emptyList()
            )
        }
        customBoard = Board(cells)
    }

    @Test
    fun constructorWithSizeShouldCreateCorrectNumberOfCells() {
        // Assert
        assertEquals(10, defaultBoard.size, "Board constructed with size 10 should have 10 cells.")
    }

    @Test
    fun constructorWithSizeShouldMarkBranchCells() {
        // Cells 3 and 7 should be marked as branch cells
        val branchCells = defaultBoard.cells.filter { it.hasBranch }
        assertEquals(2, branchCells.size, "Exactly two cells should be marked as branch cells (indices 3 and 7).")

        val branchIndices = branchCells.map { it.index }
        assertTrue(
            branchIndices.contains(3) && branchIndices.contains(7),
            "Branch cells should be at indices 3 and 7."
        )
    }

    @Test
    fun customBoardShouldHaveCorrectCellCount() {
        // The customBoard was initialized with 5 cells
        assertEquals(5, customBoard.size, "Custom board should have exactly 5 cells.")
    }

    @Test
    fun customBoardShouldAssignBranchOptionsCorrectly() {
        // Indices 0, 2, 4 (even) are marked as hasBranch = true
        // and have branchOptions = [index + 5]
        val branchCells = customBoard.cells.filter { it.hasBranch }
        assertEquals(3, branchCells.size, "Three cells should be marked with hasBranch = true (indices 0, 2, 4).")

        branchCells.forEach { cell ->
            val expectedOptions = listOf(cell.index + 5)
            assertEquals(expectedOptions, cell.branchOptions,
                "Branch options should be [index + 5] for cells with hasBranch = true.")
        }
    }


    @Test
    fun getCellShouldReturnCorrectCell() {
        // For defaultBoard, test a straightforward index
        val cellAtIndex3 = defaultBoard.getCell(3)
        assertEquals(3, cellAtIndex3.index, "defaultBoard.getCell(3) should return the cell with index 3.")

        // For customBoard, test an in-range index
        val cellAtIndex2 = customBoard.getCell(2)
        assertEquals(2, cellAtIndex2.index, "customBoard.getCell(2) should return the cell with index 2.")
    }

    @Test
    fun getCellShouldWrapIndexUsingModulo() {
        // defaultBoard has size 10, so index 12 wraps to 2
        val wrappedCell = defaultBoard.getCell(12)
        assertEquals(2, wrappedCell.index, "Index 12 should wrap around to cell index 2 for a board of size 10.")

        // customBoard has size 5, so index 7 wraps to 2
        val wrappedCellCustom = customBoard.getCell(7)
        assertEquals(2, wrappedCellCustom.index, "Index 7 should wrap around to cell index 2 for a board of size 5.")
    }



}