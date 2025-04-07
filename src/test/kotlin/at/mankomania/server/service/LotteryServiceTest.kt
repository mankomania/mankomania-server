package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class LotteryServiceTest {

    private lateinit var lotteryService: LotteryService
    private lateinit var player: Player

    @BeforeEach
    fun setUp() {
        lotteryService = LotteryService()
        player = Player("TestPlayer", balance = 100000)
    }

    @Test
    fun `processGoToField adds to pool when player has balance`() {
        assertTrue(lotteryService.processGoToField(player))
        assertEquals(5000, lotteryService.getPoolAmount())
        assertEquals(95000, player.balance)
    }

    @Test
    fun `processGoToField fails when player is bankrupt`() {
        player.balance = 0
        assertFalse(lotteryService.processGoToField(player))
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery awards pool when not empty`() {
        lotteryService.processGoToField(player) // Add 5000 to pool
        val result = lotteryService.processLandingOnLottery(player)

        assertTrue(result.success)
        assertEquals("Won 5000 from lottery!", result.message)
        assertEquals(100000, player.balance) // 100000 -5000 +5000
        assertEquals(0, lotteryService.getPoolAmount())
    }

    @Test
    fun `processLandingOnLottery takes payment when pool empty`() {
        val result = lotteryService.processLandingOnLottery(player)

        assertTrue(result.success)
        assertEquals("Paid 50000", result.message)
        assertEquals(50000, player.balance)
        assertEquals(50000, lotteryService.getPoolAmount())
    }

    @Test
    fun `processPaymentWithNotification returns correct message`() {
        val (success, message) = lotteryService.processPaymentWithNotification(player, 5000, "test")

        assertTrue(success)
        assertTrue(message.contains("test"))
        assertEquals(5000, lotteryService.getPoolAmount())
    }
}