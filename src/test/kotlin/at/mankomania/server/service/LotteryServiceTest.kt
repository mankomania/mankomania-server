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

    @Test
    fun `processPaymentWithNotification should handle exact balance payment`() {
        player.balance = 5000
        val (success, message) = lotteryService.processPaymentWithNotification(player, 5000, "exact payment")

        assertTrue(success)
        assertEquals("exact payment – 5000 added to the lottery", message)
        assertEquals(0, player.balance)
        assertTrue(lotteryService.getWinners().contains(player.name))
    }

    @Test
    fun `multiple payments accumulate in pool correctly`() {
        repeat(3) { lotteryService.processGoToField(player) }
        assertEquals(15000, lotteryService.getPoolAmount())
        assertEquals(85000, player.balance)
    }

    @Test
    fun `landing after multiple payments awards full amount`() {
        repeat(3) { lotteryService.processGoToField(player) }
        val result = lotteryService.processLandingOnLottery(player)

        assertTrue(result.success)
        assertEquals(100000, player.balance) // 100000 - 15000 + 15000
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPayment should not allow negative balance`() {
        player.balance = 4999
        val result = lotteryService.processPayment(player, 5000, "overdraft attempt")

        assertFalse(result)
        assertEquals(4999, player.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `declareWinner adds player to winners set`() {
        lotteryService.declareWinner(player)
        assertEquals(setOf(player.name), lotteryService.getWinners())
        assertTrue(lotteryService.isWinner(player))
    }

    @Test
    fun `pool remains empty after taking from empty pool`() {
        assertEquals(0, lotteryService.getPoolAmount())
        val result = lotteryService.processLandingOnLottery(player)
        assertTrue(result.success)
        assertEquals(50000, lotteryService.getPoolAmount()) // Now has 50000 from payment
    }

    @Test
    fun `processPaymentWithNotification fails for bankrupt player`() {
        player.balance = 0
        val (success, message) = lotteryService.processPaymentWithNotification(player, 5000, "test")

        assertFalse(success)
        assertEquals("Player(name=TestPlayer, position=0, balance=0) won", message)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `getCurrentLotteryAmount reflects pool changes immediately`() {
        assertEquals(0, lotteryService.getCurrentLotteryAmount())
        lotteryService.processGoToField(player)
        assertEquals(5000, lotteryService.getCurrentLotteryAmount())
        lotteryService.processLandingOnLottery(player)
        assertEquals(0, lotteryService.getCurrentLotteryAmount())
    }

}