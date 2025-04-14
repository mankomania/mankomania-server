package at.mankomania.server


import at.mankomania.server.model.Board
import at.mankomania.server.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * @author eles17
 * Unit test class for the [Player] functionality.
 */
class PlayerTest {

    private lateinit var player: Player
    private lateinit var board: Board

    @BeforeEach
    fun setup() {
        board = Board(40) { index ->
            index == 5 || index == 15
        }

        // Overwrite the default fields to include branchOptions
        val fieldsWithBranches = board.cells.map { field ->
            if (field.index == 5 || field.index == 15)
                field.copy(branchOptions = listOf(20, 25))
            else field
        }
        board = Board(fieldsWithBranches)

        player = Player(name = "blue")
    }


    // ------------------------------------------------
    // Player movement tests
    // ------------------------------------------------
    /**
     * Verifies that the player moves correctly based on dice rolls.
     */
    @Test
    fun playerMovesCorrectlyBasedOnDiceRoll (){
        player.move(4, board)
        assertEquals(4,player.position)

        player.move(6, board)
        assertEquals(10, player.position)
    }

    /**
     * Verifies that the player wraps around the board when exceeding the last field.
     */
    @Test
    fun playerWrapsAroundBoardCorrectly (){
        player.position = 38
        player.move(5,board)
        assertEquals(3,player.position)
    }

    /**
     * Verifies that landing exactly on the last field wraps back to the starting point.
     */
    @Test
    fun playerLandsExactlyOnLastField (){
        player.position = 38
        player.move(2,board)
        assertEquals(0,player.position) //40 % 40 = 0...
    }



    // ------------------------------------------------
    // Branch field detection tests
    // ------------------------------------------------
    /**
     * Verifies that landing on a branch field returns true.
     */

    /**
     * Verifies that landing on a branch field returns true.
     */
    @Test
    fun playerLandsOnBranchField() {
        player.position = 5
        assertTrue(player.hasBranch(board))
    }

    /**
     * Verifies that landing on non-branch field returns false
     */
    @Test
    fun playerLandOnNonBranchField(){
        player.position = 10
        assertFalse(player.hasBranch(board))
    }
    /**
     * Verifies that move() chooses a branch option when on a branching field.
     */
    @Test
    fun moveSelectsBranchOptionCorrectly() {
        player.position = 2
        val landedOnBranch = player.move(3, board) // lands on field 5 with branchOptions [20, 25]
        assertTrue(landedOnBranch)
        assertEquals(20, player.position) // assumes chooseBranch picks first option
    }

    /**
     * Verifies that chooseBranch() updates the player's position.
     */
    @Test
    fun chooseBranchSetsNewPosition() {
        player.chooseBranch(listOf(18, 21))
        assertEquals(18, player.position)
    }

    /**
     * Verifies that move() returns true when landing on a branch field.
     */
    @Test
    fun moveReturnsTrueWhenLandingOnBranch(){
        player.position = 2
        val landedOnBranch = player.move(3, board) // lands on field 5
        assertTrue(landedOnBranch)
    }

    /**
     * Verifies that move() returns false when landing on a non-branch field.
     */
    @Test
    fun moveReturnsFalseWhenNoBranch(){
        player.position = 0
        val landedOnBranch = player.move(2, board) // lands on field 5
        assertFalse(landedOnBranch)
    }


    // ------------------------------------------------
    // Position getter test
    // ------------------------------------------------

    /**
     * Verifies that getCurrentPosition() returns the correct value.
     */
    @Test
    fun getCurrentPositionReturnCorrectValue(){
        player.position = 22
        assertEquals(22, player.getCurrentPosition())
    }
}
// ------------------------------------------------
// Starting money tests
// ------------------------------------------------

/**
 * Verifies that the starting money is assigned correctly with the expected denominations.
 */
@Test
fun assignStartingMoneyAssignsCorrectDenominations() {
    player.assignStartingMoney()

    val expectedMoney = mapOf(
        5000 to 10,
        10000 to 5,
        50000 to 4,
        100000 to 7
    )

    assertEquals(expectedMoney, player.money)

    // Check total sum is exactly 1,000,000
    val total = player.money!!.entries.sumOf { it.key * it.value }
    assertEquals(1_000_000, total)
}

/**
 * Verifies that assignStartingMoney does not overwrite existing money.
 */
@Test
fun assignStartingMoneyIsIdempotent() {
    // Simulate existing money
    player.money = mutableMapOf(100000 to 10)

    player.assignStartingMoney()

    val expectedMoney = mapOf(100000 to 10)
    assertEquals(expectedMoney, player.money)
}
