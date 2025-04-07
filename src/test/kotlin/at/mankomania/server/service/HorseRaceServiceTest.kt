
import at.mankomania.server.MankomaniaServerApplication
import at.mankomania.server.model.Bet
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
        val result = horseRaceService.startRace(listOf())
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
    fun `calculateWinnings returns correct payouts`() {
        val bets = listOf(
            Bet(playerId = "player1", horseColor = HorseColor.RED, amount = 100),
            Bet(playerId = "player2", horseColor = HorseColor.BLUE, amount = 200),
            Bet(playerId = "player3", horseColor = HorseColor.RED, amount = 50)
        )
        val winningColor = HorseColor.RED
        val horseRaceService = HorseRaceService()

        val result = horseRaceService.calculateWinnings(bets, winningColor)

        assertEquals(200, result["player1"])
        assertEquals(0, result["player2"])
        assertEquals(100, result["player3"])
    }

    @Test
    fun `spinRoulette returns a valid HorseColor`() {
        val color = horseRaceService.spinRoulette()
        assertTrue(HorseColor.entries.contains(color))
    }

    @Test
    fun `test place bet - not enough balance`() {
        val player = Player("test-player", 0)
        horseRaceService.registerPlayer(player)
        val result = horseRaceService.placeBet("test-player", HorseColor.BLUE, 2025)
        assertFalse(result)
    }

    @Test
    fun `test place bet`() {
        val player = Player("test-player", 0)
        horseRaceService.registerPlayer(player)
        val result = horseRaceService.placeBet("test-player", HorseColor.BLUE, 2025)
        assertFalse(result)
    }
}
