package at.mankomania.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MankomaniaServerApplication

fun main(args: Array<String>) {
	runApplication<MankomaniaServerApplication>(*args)
}
