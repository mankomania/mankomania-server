/* package at.mankomania.server.controller

import at.mankomania.server.controller.dto.JoinDto
import at.mankomania.server.controller.dto.StartDto
import at.mankomania.server.manager.GameSessionManager
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@WebMvcTest(GameLobbyController::class)
class GameLobbyControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockitoBean
    private lateinit var sessionManager: GameSessionManager

    private val mapper = jacksonObjectMapper()

    @Test
    fun `POST join returns 200 and lobby state when added`() {
        val gameId = "g1"
        given(sessionManager.joinGame(gameId, "Kafka")).willReturn(true)
        given(sessionManager.getPlayers(gameId)).willReturn(listOf(
            at.mankomania.server.model.Player("Kafka")
        ))

        mvc.perform(post("/lobby/$gameId/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(JoinDto("Kafka"))))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.gameId").value(gameId))
            .andExpect(jsonPath("$.players[0]").value("Kafka"))
            .andExpect(jsonPath("$.canStart").value(false))

        verify(sessionManager).joinGame(gameId, "Kafka")
    }

    @Test
    fun `POST join returns 400 when name duplicate or full`() {
        val gameId = "g1"
        given(sessionManager.joinGame(gameId, "Woolf")).willReturn(false)
        given(sessionManager.getPlayers(gameId)).willReturn(emptyList())

        mvc.perform(post("/lobby/$gameId/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(JoinDto("Woolf"))))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `POST start returns 200 and positions when enough players`() {
        val gameId = "g1"
        val dto = StartDto(boardSize = 40)
        given(sessionManager.startSession(gameId, 40)).willReturn(listOf(0,10))

        mvc.perform(post("/lobby/$gameId/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(dto)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.gameId").value(gameId))
            .andExpect(jsonPath("$.startPositions[0]").value(0))
            .andExpect(jsonPath("$.startPositions[1]").value(10))

        verify(sessionManager).startSession(gameId, 40)
    }

    @Test
    fun `POST start returns 400 when not enough players`() {
        val gameId = "g1"
        given(sessionManager.startSession(gameId, 50)).willReturn(null)

        mvc.perform(post("/lobby/$gameId/start")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(StartDto(50))))
            .andExpect(status().isBadRequest)
    }

}

 */