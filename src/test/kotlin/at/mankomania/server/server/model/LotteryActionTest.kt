package at.mankomania.server.model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class LotteryActionTest {

    private lateinit var lotteryAction: LotteryAction

    @BeforeEach
    fun setUp() {
        lotteryAction = LotteryAction()
    }

    @Test
    fun `addToPool increases pool amount`() {
        lotteryAction.addToPool(5000)
        assertEquals(5000, lotteryAction.getPoolAmount())
    }

    @Test
    fun `addToPool throws for negative amount`() {
        assertFailsWith<IllegalArgumentException> {
            lotteryAction.addToPool(-100)
        }
    }

    @Test
    fun `takeFromPool returns current amount and resets pool`() {
        lotteryAction.addToPool(5000)
        val amount = lotteryAction.takeFromPool()
        assertEquals(5000, amount)
        assertEquals(0, lotteryAction.getPoolAmount())
    }

    @Test
    fun `takeFromPool returns zero for empty pool`() {
        assertEquals(0, lotteryAction.takeFromPool())
    }

    @Test
    fun `isEmpty returns true for empty pool`() {
        assertTrue(lotteryAction.isEmpty())
    }

    @Test
    fun `isEmpty returns false for non-empty pool`() {
        lotteryAction.addToPool(100)
        assertFalse(lotteryAction.isEmpty())
    }

    @Test
    fun `transactions list is initially empty`() {
        assertTrue(lotteryAction.transactions.isEmpty())
    }
}