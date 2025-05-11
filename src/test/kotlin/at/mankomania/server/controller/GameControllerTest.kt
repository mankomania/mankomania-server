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

    /**
     * movePlayer should do nothing if player is not found.
     * Ensures no exceptions or calls are made for invalid player input.
     */
    @Test
    fun `movePlayer should do nothing if player is not found`() {
        controller.movePlayer("Ghost", 3)
        verify(notificationService, never()).sendPlayerMoved(anyString(), anyInt())
        verify(notificationService, never()).sendPlayerLanded(anyString(), anyInt())
    }

    @Test
    fun `movePlayer on branch cell should only move`() {
        // Board mit Branch auf Feld 2
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
    /**
     * landOnCell should do nothing if player is not found.
     * Ensures that no notifications are sent when a nonexistent player is passed.
     */
    @Test
    fun `landOnCell should do nothing if player is not found`() {
        controller.landOnCell("Ghost", 3)
        verify(notificationService, never()).sendPlayerLanded(anyString(), anyInt())
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

    /**
     * Player wraps around the board.
     * Verifies that a player starting near the end of the board and moving past the last cell wraps around to the beginning.
     */
    @Test
    fun `computeMoveResult should wrap around board correctly`() {
        val player = players.find { it.name == "Toni" }!!
        player.position = 4 // last field on 5-cell board

        val result = controller.computeMoveResult("Toni", 2)

        assert(result != null)
        assert(result!!.oldPosition == 4)
        assert(result.newPosition == 1) // (4 + 2) % 5 = 1
    }

    /**
     * Player lands on a field occupied by others.
     * Verifies that the response includes the names of other players already on that field.
     */
    @Test
    fun `computeMoveResult should include players already on the field`() {
        val toni = players.find { it.name == "Toni" }!!
        val jorge = players.find { it.name == "Jorge" }!!
        toni.position = 0
        jorge.position = 2

        val result = controller.computeMoveResult("Toni", 2)

        assert(result != null)
        assert(result!!.playersOnField.contains("Jorge"))
        assert(result.playersOnField.size == 1)
    }

    /**
     * computeMoveResult should return null if player is not found.
     */
    @Test
    fun `computeMoveResult should return null if player is not found`() {
        val result = controller.computeMoveResult("Ghost", 3)
        assert(result == null)
    }

}