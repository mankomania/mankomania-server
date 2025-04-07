package at.mankomania.server.controller

import at.mankomania.server.model.Player
import at.mankomania.server.service.LotteryService
import at.mankomania.server.service.LotteryService.LotteryResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LotteryControllerTest {

    private lateinit var lotteryController: LotteryController
    private lateinit var mockLotteryService: MockLotteryService
    private lateinit var testPlayer: Player

    @BeforeEach
    fun setUp() {
        mockLotteryService = MockLotteryService()
        lotteryController = LotteryController(mockLotteryService)
        testPlayer = Player("TestPlayer", balance = 100000)
    }

    @Test
    fun `getPoolAmount returns correct pool amount`() {
        mockLotteryService.mockPoolAmount = 5000

        val result = lotteryController.getPoolAmount()

        assertEquals(mapOf("amount" to 5000), result)
    }

    @Test
    fun `processPayment with goToField reason calls correct service method`() {
        mockLotteryService.mockProcessGoToField = true
        mockLotteryService.mockPoolAmount = 5000

        val result = lotteryController.processPayment("123", testPlayer, "goToField")

        assertTrue(result["success"] as Boolean)
        assertEquals(5000, result["poolAmount"])
        assertEquals(100000, result["playerBalance"])
        assertTrue(mockLotteryService.processGoToFieldCalled)
    }

    @Test
    fun `processPayment with passing reason calls correct service method`() {
        mockLotteryService.mockProcessPassingLottery = true
        mockLotteryService.mockPoolAmount = 5000

        val result = lotteryController.processPayment("123", testPlayer, "passing")

        assertTrue(result["success"] as Boolean)
        assertEquals(5000, result["poolAmount"])
        assertEquals(100000, result["playerBalance"])
        assertTrue(mockLotteryService.processPassingLotteryCalled)
    }

    @Test
    fun `processPayment with invalid reason returns failure`() {
        val result = lotteryController.processPayment("123", testPlayer, "invalid")

        assertFalse(result["success"] as Boolean)
        assertFalse(mockLotteryService.processGoToFieldCalled)
        assertFalse(mockLotteryService.processPassingLotteryCalled)
    }

    @Test
    fun `processLanding returns service result`() {
        mockLotteryService.mockLandingResult = LotteryResult(true, "You won!")
        mockLotteryService.mockPoolAmount = 5000

        val result = lotteryController.processLanding(testPlayer)

        assertTrue(result["success"] as Boolean)
        assertEquals("You won!", result["message"])
        assertEquals(5000, result["poolAmount"])
        assertEquals(100000, result["playerBalance"])
    }

    @Test
    fun `getCurrentLotteryAmount returns correct amount`() {
        mockLotteryService.mockCurrentLotteryAmount = 7500

        val response = lotteryController.getCurrentLotteryAmount()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(7500, response.body)
    }

    @Test
    fun `processPaymentWithNotification returns service response`() {
        mockLotteryService.mockPaymentNotification = Pair(true, "Payment successful")
        mockLotteryService.mockPoolAmount = 10000

        val response = lotteryController.processPaymentWithNotification(testPlayer, "test")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body?.get("success") as Boolean)
        assertEquals("Payment successful", response.body?.get("message"))
        assertEquals(10000, response.body?.get("newPoolAmount"))
    }

    private class MockLotteryService : LotteryService() {
        var mockPoolAmount = 0
        var mockCurrentLotteryAmount = 0
        var mockProcessGoToField = false
        var mockProcessPassingLottery = false
        var mockLandingResult = LotteryResult(false, "")
        var mockPaymentNotification = Pair(false, "")

        var processGoToFieldCalled = false
        var processPassingLotteryCalled = false

        override fun getPoolAmount(): Int = mockPoolAmount
        override fun getCurrentLotteryAmount(): Int = mockCurrentLotteryAmount

        override fun processGoToField(player: Player): Boolean {
            processGoToFieldCalled = true
            return mockProcessGoToField
        }

        override fun processPassingLottery(player: Player): Boolean {
            processPassingLotteryCalled = true
            return mockProcessPassingLottery
        }

        override fun processLandingOnLottery(player: Player): LotteryResult {
            return mockLandingResult
        }

        override fun processPaymentWithNotification(player: Player, amount: Int, reason: String): Pair<Boolean, String> {
            return mockPaymentNotification
        }
    }
}