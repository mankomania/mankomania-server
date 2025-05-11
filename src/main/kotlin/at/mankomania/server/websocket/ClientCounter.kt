package at.mankomania.server.websocket

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class ClientCounter {
    private val counter = AtomicInteger(0)
    fun increment(): Int = counter.incrementAndGet()
    fun decrement(): Int = counter.decrementAndGet()
    fun get(): Int = counter.get()
}
