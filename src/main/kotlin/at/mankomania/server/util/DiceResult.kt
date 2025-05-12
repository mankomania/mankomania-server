package at.mankomania.server.util
// this is a data holder. It stors and transports the result of a roll
//both dice and their sum
data class DiceResult(val die1: Int, val die2: Int) {
    val sum: Int get() = die1 + die2
}
