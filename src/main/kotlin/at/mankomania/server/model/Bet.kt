package at.mankomania.server.model

/**
 * Represents a bet placed by a player on a specific horse color.
 */
data class Bet(
    val playerId: String,
    val horseColor: HorseColor,
    val amount: Int
)