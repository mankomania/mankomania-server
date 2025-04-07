package at.mankomania.server

import at.mankomania.server.model.Player
import at.mankomania.server.service.LotteryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class LotteryServiceTest {
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

    @Test
    fun processGoToField() {
        val initialPool = lotteryService.getPoolAmount()
        assertTrue(lotteryService.processGoToField(richPlayer))
        assertEquals(95000, richPlayer.balance)
        assertEquals(initialPool + 5000, lotteryService.getPoolAmount())
    }

    @Test
    fun processPassingLottery() {
        val initialPool = lotteryService.getPoolAmount()
        assertTrue(lotteryService.processPassingLottery(richPlayer))
        assertEquals(95000, richPlayer.balance)
        assertEquals(initialPool + 5000, lotteryService.getPoolAmount())
    }

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

    @Test
    fun landingOnEmptyPool() {
        assertEquals(0, lotteryService.getPoolAmount())
        val result = lotteryService.processLandingOnLottery(richPlayer)
        assertTrue(result.success)
        assertEquals(50000, richPlayer.balance)
        assertEquals(50000, lotteryService.getPoolAmount())
    }

    @Test
    fun playerCantReceiveMoneyTwice() {
        lotteryService.processGoToField(richPlayer)
        lotteryService.processLandingOnLottery(richPlayer)
        val result = lotteryService.processLandingOnLottery(richPlayer)
        assertTrue(result.success)
        assertEquals(50000, richPlayer.balance)
    }

    @Test
    fun playerBecomesWinnerWithNoMoney() {
        val player = Player("almostBankrupt", 5000)
        assertTrue(lotteryService.processGoToField(player))
        assertEquals(0, player.balance)
        assertFalse(lotteryService.processPassingLottery(player))
    }

}