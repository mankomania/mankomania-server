package at.mankomania.server.websocket

import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class PresenceListener(
    private val counter: ClientCounter,
    private val template: SimpMessagingTemplate
) {

    @EventListener
    fun handleConnect(event: SessionConnectEvent) {
        broadcast(counter.increment())
    }

    @EventListener
    fun handleDisconnect(event: SessionDisconnectEvent) {
        broadcast(counter.decrement())
    }

    private fun broadcast(current: Int) {
        template.convertAndSend("/topic/clientCount", current)
    }
}
