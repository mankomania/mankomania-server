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
}