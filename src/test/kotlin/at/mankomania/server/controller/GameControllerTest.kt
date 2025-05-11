package at.mankomania.server.controller

import at.mankomania.server.controller.dto.GameStateDto
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
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GameControllerTest {

    private lateinit var board: Board
    private lateinit var players: List<Player>
    private lateinit var controller: GameController

    @Mock
    private lateinit var notificationService: NotificationService

    @Mock
    private lateinit var bankService: BankService

    @BeforeEach
    fun setUp() {
        board   = BoardFactory.createBoard(5) { false }
        players = listOf(Player("Toni"), Player("Jorge"))
        controller = GameController(board, players, bankService, notificationService)
    }

    @Test
    fun `startGame should broadcast correct initial state`() {
        val expectedDto = GameStateDto(players, board.cells)

        controller.startGame()

        // direkte Objektreferenz, Data-Klassen haben equals() implementiert
        verify(notificationService).sendGameState(expectedDto)
    }

    @Test
    fun `movePlayer on non-branch cell should land then move`() {
        controller.movePlayer("Toni", 2)
        verify(notificationService).sendPlayerLanded("Toni", 2)
        verify(notificationService).sendPlayerMoved("Toni", 2)
    }

    @Test
    fun `movePlayer on branch cell should only move`() {
        board = Board(
            listOf(
                BoardCell(0, hasBranch = false),
                BoardCell(1, hasBranch = false),
                BoardCell(2, hasBranch = true, branchOptions = listOf(4)),
                BoardCell(3, hasBranch = false),
                BoardCell(4, hasBranch = false)
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
}
