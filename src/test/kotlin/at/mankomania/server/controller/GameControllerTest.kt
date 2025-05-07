package at.mankomania.server.controller

import at.mankomania.server.model.Board
import at.mankomania.server.model.BoardCell
import at.mankomania.server.model.BoardFactory
import at.mankomania.server.model.Player
import at.mankomania.server.service.NotificationService
import at.mankomania.server.service.BankService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.verify


@ExtendWith(MockitoExtension::class)
class GameControllerTest {

    private lateinit var board: Board
    private lateinit var players: List<Player>
    private lateinit var bankService: BankService
    private lateinit var notificationService: NotificationService
    private lateinit var controller: GameController

    @BeforeEach
    fun setUp() {
        board = BoardFactory.createBoard(5) { false }
        players = listOf(Player("Toni"), Player("Jorge"))
        bankService = mock(BankService::class.java)
        notificationService = mock(NotificationService::class.java)
        controller = GameController(board, players, bankService, notificationService)
    }

    @Test
    fun `startGame should broadcast initial state`() {
        controller.startGame()
        verify(notificationService).sendGameState(players)
    }

    @Test
    fun `movePlayer on non-branch cell should land then move`() {
        controller.movePlayer("Toni", 2)
        verify(notificationService).sendPlayerLanded("Toni", 2)
        verify(notificationService).sendPlayerMoved("Toni", 2)
    }

    @Test
    fun `movePlayer on branch cell should only move`() {
        // Board mit Branch auf Feld 2
        board = Board(
            listOf(
                BoardCell(index = 0, hasBranch = false),
                BoardCell(index = 1, hasBranch = false),
                BoardCell(index = 2, hasBranch = true, branchOptions = listOf(4)),
                BoardCell(index = 3, hasBranch = false),
                BoardCell(index = 4, hasBranch = false)
            )
        )
        controller = GameController(board, players, bankService, notificationService)

        controller.movePlayer("Toni", 2)
        verify(notificationService).sendPlayerMoved("Toni", 4)
        verify(notificationService, never()).sendPlayerLanded(anyString(), anyInt())
    }

    @Test
    fun `landOnCell always sends landing notification`() {
        controller.landOnCell("Jorge", 3)
        verify(notificationService).sendPlayerLanded("Jorge", 3)
    }

    @Test
    fun `computeMoveResult should return correct MoveResult for a player move`() {
        // Arrange: Toni is at position 0, board size is 5
        val player = players.find { it.name == "Toni" }!!
        player.position = 0
// Act: move 2 steps forward
        val result = controller.computeMoveResult("Toni", 2)

        // Assert: result is not null and values are as expected
        assert(result != null)
        assert(result!!.oldPosition == 0)
        assert(result.newPosition == 2)
        assert(result.fieldType == "NoAction") // Default as no action set
        assert(result.fieldDescription == "No description available")
        assert(result.playersOnField.isEmpty())
    }
}