import at.mankomania.server.MankomaniaServerApplication
import at.mankomania.server.model.HorseColor
import at.mankomania.server.model.Player
import at.mankomania.server.service.HorseRaceService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(classes = [MankomaniaServerApplication::class])
class HorseRaceServiceTest {

    @Autowired
    private lateinit var horseRaceService: HorseRaceService

    @Test
    fun `test spin roulette`() {
        val result = horseRaceService.spinRoulette()
        assertNotNull(result)
    }

    @Test
    fun `test start race`() {
        val result = horseRaceService.startRace(listOf(), mapOf())
        assertNotNull(result)
    }

    @Test
    fun `test register player`() {
        val balance = 2025
        horseRaceService.registerPlayer(Player("test-player", 0, balance))
        val player = horseRaceService.getPlayer("test-player")

        assertNotNull(player)
        assertEquals(balance, player!!.balance)
    }

    @Test
    fun `test place bet - player does not exist`() {
        val result = horseRaceService.placeBet("non-existent-player", HorseColor.BLUE, 2025)
        assertFalse(result)
    }

    @Test
    fun `test place bet - not enough balance`() {
        horseRaceService.registerPlayer(Player("test-player", 0))
        val result = horseRaceService.placeBet("test-player", HorseColor.BLUE, 2025)
        assertFalse(result)
    }

    @Test
    fun `test place bet`() {
        horseRaceService.registerPlayer(Player("test-player", 0, 2025))
        val result = horseRaceService.placeBet("test-player", HorseColor.BLUE, 2025)
        assertTrue(result)
    }
}