package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class LotteryServiceTest {

    private lateinit var lotteryService: LotteryService
    private lateinit var player1: Player
    private lateinit var player2: Player

    @BeforeEach
    fun setUp() {
        lotteryService = LotteryService()
        player1 = Player("Player1", position = 0, balance = 100000)
        player2 = Player("Player2", position = 0, balance = 50000)
    }

    @Test
    fun `getPoolAmount should return 0 initially`() {
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processGoToField should deduct 5000 when successful`() {
        assertTrue(lotteryService.processGoToField(player1))
        assertEquals(95000, player1.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processGoToField should fail when player is bankrupt`() {
        player1.balance = 0
        assertFalse(lotteryService.processGoToField(player1))
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPassingLottery should deduct 5000 when successful`() {
        assertTrue(lotteryService.processPassingLottery(player1))
        assertEquals(95000, player1.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery should award pool when not empty`() {
        lotteryService.processPassingLottery(player2) // Add 5000 to pool
        val result = lotteryService.processLandingOnLottery(player1)

        assertTrue(result.success)
        assertEquals("Won 5000 from lottery!", result.message)
        assertEquals(105000, player1.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery should deduct 50000 when pool empty`() {
        val result = lotteryService.processLandingOnLottery(player1)

        assertTrue(result.success)
        assertEquals("Paid 50000", result.message)
        assertEquals(50000, player1.balance)
        assertEquals(50000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery should declare winner when cannot pay 50000`() {
        player1.balance = 10000
        val result = lotteryService.processLandingOnLottery(player1)

        assertFalse(result.success)
        assertEquals("Player has won the game", result.message)
        assertEquals(0, player1.balance)
        assertTrue(lotteryService.getWinners().contains(player1.name))
    }

    @Test
    fun `processPayment should handle exact balance payment`() {
        player1.balance = 5000
        assertTrue(lotteryService.processPayment(player1, 5000, "exact payment"))
        assertEquals(0, player1.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
        assertTrue(lotteryService.getWinners().contains(player1.name))
    }

    @Test
    fun `processPaymentWithNotification should return correct message on success`() {
        val (success, message) = lotteryService.processPaymentWithNotification(player1, 10000, "test")
        assertTrue(success)
        assertEquals("test – 10000 added to the lottery", message)
        assertEquals(90000, player1.balance)
        assertEquals(10000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPaymentWithNotification should handle multiple winners`() {
        lotteryService.processPaymentWithNotification(player1, 100000, "bankrupt")
        lotteryService.processPaymentWithNotification(player2, 50000, "bankrupt")

        assertEquals(2, lotteryService.getWinners().size)
        assertTrue(lotteryService.getWinners().containsAll(listOf(player1.name, player2.name)))
    }

    @Test
    fun `declareWinner should add player to winners`() {
        lotteryService.declareWinner(player1)
        assertTrue(lotteryService.isWinner(player1))
        assertEquals(1, lotteryService.getWinners().size)
    }

    @Test
    fun `isWinner should return false for player with positive balance`() {
        assertFalse(lotteryService.isWinner(player1))
    }

    @Test
    fun `getCurrentLotteryAmount should reflect pool changes`() {
        assertEquals(0, lotteryService.getCurrentLotteryAmount())
        lotteryService.processGoToField(player1)
        assertEquals(5000, lotteryService.getCurrentLotteryAmount())
        lotteryService.processLandingOnLottery(player2)
        assertEquals(0, lotteryService.getCurrentLotteryAmount())
    }

    @Test
    fun `LotteryResult should hold correct values`() {
        val successResult = LotteryService.LotteryResult(true, "Success")
        assertTrue(successResult.success)
        assertEquals("Success", successResult.message)

        val failResult = LotteryService.LotteryResult(false, "Failed")
        assertFalse(failResult.success)
        assertEquals("Failed", failResult.message)
    }

    @Test
    fun `pool should handle multiple additions`() {
        repeat(3) { lotteryService.processGoToField(player1) }
        assertEquals(15000, lotteryService.getPoolAmount())
        assertEquals(85000, player1.balance)
    }

    @Test
    fun `landing should fully empty pool`() {
        repeat(3) { lotteryService.processGoToField(player1) }
        lotteryService.processLandingOnLottery(player2)
        assertEquals(0, lotteryService.getPoolAmount())
        assertEquals(65000, player2.balance)
    }

    @Test
    fun `winner should not be able to participate`() {
        player1.balance = 0
        assertFalse(lotteryService.processGoToField(player1))
        assertFalse(lotteryService.processPassingLottery(player1))

        val result = lotteryService.processLandingOnLottery(player1)
        assertFalse(result.success)
        assertEquals("Player has already won", result.message)
    }
}