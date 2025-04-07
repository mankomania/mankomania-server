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

}