package at.mankomania.server.service

import at.mankomania.server.model.Player
import at.mankomania.server.service.LotteryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class LotteryServiceTest {
    private lateinit var lotteryService: LotteryService
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        lotteryService = LotteryService()
        player = Player("TestPlayer", position = 0, balance = 100000)
    }

    @Test
    fun `processGoToField should add to pool when player has sufficient balance`() {
        val result = lotteryService.processGoToField(player)

        assertTrue(result)
        assertEquals(95000, player.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processGoToField should not add to pool when player is already winner`() {
        player.balance = 0
        val result = lotteryService.processGoToField(player)

        assertFalse(result)
        assertEquals(0, player.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPassingLottery should add to pool when player has sufficient balance`() {
        val result = lotteryService.processPassingLottery(player)

        assertTrue(result)
        assertEquals(95000, player.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPassingLottery should not add to pool when player is already winner`() {
        player.balance = 0
        val result = lotteryService.processPassingLottery(player)

        assertFalse(result)
        assertEquals(0, player.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery should award pool when pool is not empty`() {
        // First add to pool
        lotteryService.processGoToField(player)
        val initialPool = lotteryService.getPoolAmount()

        // Now land on lottery
        val result = lotteryService.processLandingOnLottery(player)

        assertTrue(result.success)
        assertEquals("Won $initialPool from lottery!", result.message)
        assertEquals(100000, player.balance) // 100000 - 5000 + 5000
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery should take payment when pool is empty and player has sufficient balance`() {
        val result = lotteryService.processLandingOnLottery(player)

        assertTrue(result.success)
        assertEquals("Paid 50000", result.message)
        assertEquals(50000, player.balance)
        assertEquals(50000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery should declare winner when pool is empty and player has insufficient balance`() {
        player.balance = 1000
        val result = lotteryService.processLandingOnLottery(player)

        assertFalse(result.success)
        assertEquals("Player has won the game", result.message)
        assertEquals(0, player.balance)
        assertTrue(lotteryService.getWinners().contains(player.name))
    }

    @Test
    fun `processLandingOnLottery should not allow already winners to win again`() {
        player.balance = 0
        val result = lotteryService.processLandingOnLottery(player)

        assertFalse(result.success)
        assertEquals("Player has already won", result.message)
    }

    @Test
    fun `processPayment should add to pool and declare winner if balance goes to zero`() {
        player.balance = 5000
        val result = lotteryService.processPayment(player, 5000, "test payment")

        assertTrue(result)
        assertEquals(0, player.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
        assertTrue(lotteryService.getWinners().contains(player.name))
    }

    @Test
    fun `processPayment should return false when insufficient balance`() {
        player.balance = 4999
        val result = lotteryService.processPayment(player, 5000, "test payment")

        assertFalse(result)
        assertEquals(4999, player.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `getCurrentLotteryAmount should return correct pool amount`() {
        assertEquals(0, lotteryService.getCurrentLotteryAmount())

        lotteryService.processGoToField(player)
        assertEquals(5000, lotteryService.getCurrentLotteryAmount())
    }

    @Test
    fun `processPaymentWithNotification should return success message when payment succeeds`() {
        val (success, message) = lotteryService.processPaymentWithNotification(player, 5000, "test reason")

        assertTrue(success)
        assertEquals("test reason – 5000 added to the lottery", message)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPaymentWithNotification should return failure message when payment fails`() {
        player.balance = 0
        val (success, message) = lotteryService.processPaymentWithNotification(player, 5000, "test reason")

        assertFalse(success)
        assertEquals("Player(name=TestPlayer, position=0, balance=0) won", message)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `getWinners should return empty set initially`() {
        assertTrue(lotteryService.getWinners().isEmpty())
    }

    @Test
    fun `getWinners should return players who have won`() {
        player.balance = 5000
        lotteryService.processPayment(player, 5000, "bankrupt")

        assertEquals(setOf(player.name), lotteryService.getWinners())
    }

    @Test
    fun `isWinner should return true for players with zero balance`() {
        player.balance = 0
        assertTrue(lotteryService.isWinner(player))
    }

    @Test
    fun `isWinner should return true for players in winners set`() {
        lotteryService.declareWinner(player)
        assertTrue(lotteryService.isWinner(player))
    }

    @Test
    fun `isWinner should return false for regular players`() {
        assertFalse(lotteryService.isWinner(player))
    }
}









    /*
    private lateinit var lotteryService: LotteryService
    private lateinit var richPlayer: Player
    private lateinit var poorPlayer: Player
    private lateinit var bankruptPlayer: Player

    @BeforeEach
    fun setup() {
        lotteryService = LotteryService()
        richPlayer = Player("rich", 100000)
        poorPlayer = Player("poor", 4000)
        bankruptPlayer = Player("bankrupt", 0)
    }

    // Payment when using “go to” fields
    @Test
    fun processGoToField() {
        val initialPool = lotteryService.getPoolAmount()
        assertTrue(lotteryService.processGoToField(richPlayer))
        assertEquals(95000, richPlayer.balance)
        assertEquals(initialPool + 5000, lotteryService.getPoolAmount())
    }

    // Payment when crossing the lottery field
    @Test
    fun processPassingLottery() {
        val initialPool = lotteryService.getPoolAmount()
        assertTrue(lotteryService.processPassingLottery(richPlayer))
        assertEquals(95000, richPlayer.balance)
        assertEquals(initialPool + 5000, lotteryService.getPoolAmount())
    }

    // Correct payout when landing directly on the field
    @Test
    fun landingOnLotteryWithMoney() {
        val initialPool = lotteryService.getPoolAmount()
        lotteryService.processGoToField(richPlayer)
        val poolAfterPayment = lotteryService.getPoolAmount()
        assertEquals(initialPool + 5000, poolAfterPayment)
        val initialBalance = richPlayer.balance
        val result = lotteryService.processLandingOnLottery(richPlayer)
        assertTrue(result.success)
        assertEquals(initialBalance + poolAfterPayment, richPlayer.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    // Pool update logic and edge cases (e.g. empty pool)
    @Test
    fun landingOnEmptyPool() {
        assertEquals(0, lotteryService.getPoolAmount())
        val result = lotteryService.processLandingOnLottery(richPlayer)
        assertTrue(result.success)
        assertEquals(50000, richPlayer.balance)
        assertEquals(50000, lotteryService.getPoolAmount())
    }

    // Tests verify that the player never receives money more than once per landing event
    @Test
    fun playerCantReceiveMoneyTwice() {
        lotteryService.processGoToField(richPlayer)
        lotteryService.processLandingOnLottery(richPlayer)
        val result = lotteryService.processLandingOnLottery(richPlayer)
        assertTrue(result.success)
        assertEquals(50000, richPlayer.balance)
    }

    // Test if the winner is chosen correctly
    @Test
    fun playerBecomesWinnerWithNoMoney() {
        val player = Player("almostBankrupt", 5000)
        assertTrue(lotteryService.processGoToField(player))
        assertEquals(0, player.balance)
        assertFalse(lotteryService.processPassingLottery(player))
    }
*/
