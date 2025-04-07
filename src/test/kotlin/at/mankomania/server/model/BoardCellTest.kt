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


}