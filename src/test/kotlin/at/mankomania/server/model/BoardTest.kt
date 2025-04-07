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
}