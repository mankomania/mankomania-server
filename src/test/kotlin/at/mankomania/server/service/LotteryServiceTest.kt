package at.mankomania.server.service

import at.mankomania.server.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class LotteryServiceTest {

    private lateinit var service: LotteryService
    private lateinit var richPlayer: Player
    private lateinit var poorPlayer: Player

    @BeforeEach
    fun setup() {
        service = LotteryService()
        richPlayer = Player("Rich", balance = 150000)
        poorPlayer = Player("Poor", balance = 4000)
    }

    // Pool amount tests
    @Test
    fun `pool starts empty`() {
        assertEquals(0, service.getPoolAmount())
    }

    // GoToField tests
    @Test
    fun `goToField deducts 5000 from player`() {
        assertTrue(service.processGoToField(richPlayer))
        assertEquals(145000, richPlayer.balance)
        assertEquals(5000, service.getPoolAmount())
    }

    @Test
    fun `goToField fails when player cannot pay`() {
        assertFalse(service.processGoToField(poorPlayer))
        assertEquals(4000, poorPlayer.balance)
        assertEquals(0, service.getPoolAmount())
    }

    // Passing lottery tests
    @Test
    fun `passingLottery deducts 5000`() {
        assertTrue(service.processPassingLottery(richPlayer))
        assertEquals(145000, richPlayer.balance)
    }

    // Landing tests
    @Test
    fun `landing awards full pool`() {
        service.processGoToField(richPlayer) // Pool = 5000
        val result = service.processLandingOnLottery(poorPlayer)

        assertTrue(result.success)
        assertEquals(9000, poorPlayer.balance) // 4000 + 5000
        assertEquals(0, service.getPoolAmount())
    }

    @Test
    fun `landing pays 50000 when pool empty`() {
        val result = service.processLandingOnLottery(richPlayer)

        assertTrue(result.success)
        assertEquals(100000, richPlayer.balance) // 150000 - 50000
        assertEquals(50000, service.getPoolAmount())
    }

    @Test
    fun `landing declares winner when cannot pay`() {
        val result = service.processLandingOnLottery(poorPlayer)

        assertFalse(result.success)
        assertEquals(0, poorPlayer.balance)
        assertTrue(service.getWinners().contains(poorPlayer.name))
    }

    // Winner tests
    @Test
    fun `winners list contains bankrupt players`() {
        service.processLandingOnLottery(poorPlayer)
        assertEquals(setOf(poorPlayer.name), service.getWinners())
    }

    @Test
    fun `winners cannot participate`() {
        poorPlayer.balance = 0
        assertFalse(service.processGoToField(poorPlayer))
        assertFalse(service.processPassingLottery(poorPlayer))
    }

    // Payment tests
    @Test
    fun `payment notification returns correct message`() {
        val (success, message) = service.processPaymentWithNotification(
            richPlayer,
            10000,
            "test"
        )

        assertTrue(success)
        assertTrue(message.contains("test"))
        assertEquals(140000, richPlayer.balance)
    }

    // Edge cases
    @Test
    fun `multiple payments accumulate pool`() {
        repeat(3) { service.processGoToField(richPlayer) }
        assertEquals(15000, service.getPoolAmount())
    }

    @Test
    fun `exact balance payment works`() {
        val exactPlayer = Player("Exact", balance = 5000)
        assertTrue(service.processPayment(exactPlayer, 5000, "exact"))
        assertEquals(0, exactPlayer.balance)
    }

    // Internal state tests
    @Test
    fun `pool resets after award`() {
        service.processGoToField(richPlayer)
        service.processLandingOnLottery(poorPlayer)
        assertEquals(0, service.getPoolAmount())
    }

    @Test
    fun `currentLotteryAmount matches pool`() {
        service.processGoToField(richPlayer)
        assertEquals(5000, service.getCurrentLotteryAmount())
    }
}