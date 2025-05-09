package at.mankomania.server.service
import at.mankomania.server.controller.HorseRaceController
import at.mankomania.server.model.Bet
import at.mankomania.server.model.HorseColor
import at.mankomania.server.model.Player
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyList
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(HorseRaceController::class)
class HorseRaceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var horseRaceService: HorseRaceService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test register player`() {
        val player = Player("player1", 0, 100)
        val playerJson = objectMapper.writeValueAsString(player)

        mockMvc.perform(post("/horse-race/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(playerJson))
            .andExpect(status().isOk)
            .andExpect(content().string("Player registered successfully."))
    }

    @Test
    fun `test place bet successful`() {
        val bet = Bet("player1", HorseColor.RED, 50)
        val betJson = objectMapper.writeValueAsString(bet)

        `when`(horseRaceService.placeBet("player1", HorseColor.RED, 50)).thenReturn(true)

        mockMvc.perform(post("/horse-race/place-bet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(betJson))
            .andExpect(status().isOk)
            .andExpect(content().string("Bet placed successfully."))
    }

    @Test
    fun `test place bet unsuccessful`() {
        val bet = Bet("player1", HorseColor.RED, 5000)
        val betJson = objectMapper.writeValueAsString(bet)

        `when`(horseRaceService.placeBet("player1", HorseColor.RED, 5000)).thenReturn(false)

        mockMvc.perform(post("/horse-race/place-bet")
            .contentType(MediaType.APPLICATION_JSON)
            .content(betJson))
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Error placing the bet."))
    }

    @Test
    fun `test start race`() {
        val winner = HorseColor.RED
        val payouts = mapOf("player1" to 200)

        `when`(horseRaceService.startRace(anyList())).thenReturn(Pair(winner, payouts))

        mockMvc.perform(post("/horse-race/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"bets": [], "players": {}}"""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.winner").value(HorseColor.RED.name))
            .andExpect(jsonPath("$.payouts.player1").value(200))
    }

    @Test
    fun `test get player found`() {
        val player = Player("player1", 0, 150)
        `when`(horseRaceService.getPlayer("player1")).thenReturn(player)

        mockMvc.perform(get("/horse-race/player/player1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("player1"))
            .andExpect(jsonPath("$.position").value(0))
            .andExpect(jsonPath("$.balance").value(150))
    }

    @Test
    fun `test get player not found`() {
        `when`(horseRaceService.getPlayer("nonexistent")).thenReturn(null)

        mockMvc.perform(get("/horse-race/player/nonexistent"))
            .andExpect(status().isNotFound)
    }
}
