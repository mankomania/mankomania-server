package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class LotteryServiceTest {
    private lateinit var lotteryService: LotteryService
    private lateinit var normalPlayer: Player
    private lateinit var richPlayer: Player
    private lateinit var poorPlayer: Player
    private lateinit var exactBalancePlayer: Player
    private lateinit var bankruptPlayer: Player

    @BeforeEach
    fun setUp() {
        lotteryService = LotteryService()
        normalPlayer = Player("Normal", balance = 100000)
        richPlayer = Player("Rich", balance = 200000)
        poorPlayer = Player("Poor", balance = 3000)
        exactBalancePlayer = Player("Exact", balance = 5000)
        bankruptPlayer = Player("Bankrupt", balance = 0)
    }

    @Test
    fun `processGoToField deducts 5000 from player with sufficient balance`() {
        assertTrue(lotteryService.processGoToField(normalPlayer))
        assertEquals(95000, normalPlayer.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processGoToField fails for bankrupt player`() {
        assertFalse(lotteryService.processGoToField(bankruptPlayer))
        assertEquals(0, bankruptPlayer.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPassingLottery deducts 5000 from rich player`() {
        assertTrue(lotteryService.processPassingLottery(richPlayer))
        assertEquals(195000, richPlayer.balance)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPassingLottery fails for player with exact balance when bankrupting`() {
        assertTrue(lotteryService.processPassingLottery(exactBalancePlayer))
        assertEquals(0, exactBalancePlayer.balance)
        assertTrue(lotteryService.getWinners().contains(exactBalancePlayer.name))
    }

    @Test
    fun `landing awards full pool to normal player`() {
        lotteryService.processGoToField(richPlayer) // Pool = 5000
        val result = lotteryService.processLandingOnLottery(normalPlayer)

        assertTrue(result.success)
        assertEquals(105000, normalPlayer.balance) // 100000 + 5000
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `landing pays 50000 when pool empty for rich player`() {
        val result = lotteryService.processLandingOnLottery(richPlayer)

        assertTrue(result.success)
        assertEquals(150000, richPlayer.balance) // 200000 - 50000
        assertEquals(50000, lotteryService.getPoolAmount())
    }

    @Test
    fun `landing declares poor player winner when cannot pay`() {
        val result = lotteryService.processLandingOnLottery(poorPlayer)

        assertFalse(result.success)
        assertEquals(0, poorPlayer.balance)
        assertTrue(lotteryService.getWinners().contains(poorPlayer.name))
    }

    @Test
    fun `multiple winners are tracked correctly`() {
        lotteryService.processLandingOnLottery(poorPlayer)
        lotteryService.processLandingOnLottery(exactBalancePlayer)

        assertEquals(2, lotteryService.getWinners().size)
        assertTrue(lotteryService.getWinners().containsAll(setOf(poorPlayer.name, exactBalancePlayer.name)))
    }

    @Test
    fun `payment notification returns correct format`() {
        val (success, message) = lotteryService.processPaymentWithNotification(
            normalPlayer,
            10000,
            "special"
        )

        assertTrue(success)
        assertEquals("special – 10000 added to the lottery", message)
        assertEquals(90000, normalPlayer.balance)
        assertEquals(10000, lotteryService.getPoolAmount())
    }

    @Test
    fun `exact balance payment works and declares winner`() {
        assertTrue(lotteryService.processPayment(exactBalancePlayer, 5000, "exact"))
        assertEquals(0, exactBalancePlayer.balance)
        assertTrue(lotteryService.isWinner(exactBalancePlayer))
    }

    @Test
    fun `pool accumulates from multiple players`() {
        lotteryService.processGoToField(normalPlayer)
        lotteryService.processPassingLottery(richPlayer)

        assertEquals(10000, lotteryService.getPoolAmount())
    }

    @Test
    fun `bankrupt player cannot perform any lottery actions`() {
        assertFalse(lotteryService.processGoToField(bankruptPlayer))
        assertFalse(lotteryService.processPassingLottery(bankruptPlayer))
        val result = lotteryService.processLandingOnLottery(bankruptPlayer)
        assertFalse(result.success)
    }

    @Test
    fun `winner cannot collect from pool`() {
        lotteryService.processGoToField(richPlayer) // Add to pool
        val result = lotteryService.processLandingOnLottery(bankruptPlayer)
        assertFalse(result.success)
        assertEquals(5000, lotteryService.getPoolAmount())
    }

    @Test
    fun `complex sequence maintains correct state`() {
        // Initial payments
        lotteryService.processGoToField(normalPlayer) // +5000
        lotteryService.processPassingLottery(richPlayer) // +5000

        // First landing
        lotteryService.processLandingOnLottery(poorPlayer) // Award 10000
        assertEquals(13000, poorPlayer.balance) // 3000 + 10000

        // Subsequent operations
        lotteryService.processGoToField(richPlayer) // +5000
        val result = lotteryService.processLandingOnLottery(normalPlayer)

        assertTrue(result.success)
        assertEquals(100000, normalPlayer.balance) // 100000 -5000 +10000 -5000 +5000
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `player balance never goes negative`() {
        assertFalse(lotteryService.processPayment(poorPlayer, 5000, "overdraft"))
        assertEquals(3000, poorPlayer.balance)
        assertEquals(0, lotteryService.getPoolAmount())
    }
}