package at.mankomania.server.websocket

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@ExtendWith(MockitoExtension::class)
class PresenceListenerTest {

    // Mockito mock, relaxed by default
    private val template: SimpMessagingTemplate = mock(SimpMessagingTemplate::class.java)
    private val counter = ClientCounter()

    private lateinit var listener: PresenceListener

    @BeforeEach
    fun setUp() {
        listener = PresenceListener(counter, template)
    }

    @Test
    fun `handleConnect increments counter and broadcasts`() {
        listener.handleConnect(mock(SessionConnectEvent::class.java))

        assertEquals(1, counter.get())
        verify(template).convertAndSend("/topic/clientCount", 1)
    }

    @Test
    fun `handleDisconnect decrements counter and broadcasts`() {
        // first client joins
        listener.handleConnect(mock(SessionConnectEvent::class.java))
        clearInvocations(template)

        // then leaves
        listener.handleDisconnect(mock(SessionDisconnectEvent::class.java))

        assertEquals(0, counter.get())
        verify(template).convertAndSend("/topic/clientCount", 0)
    }
}
