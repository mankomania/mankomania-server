package at.mankomania.server.controller.dto
/**
 * @file MoveRequest.kt
 * @author eles17
 * @since 7.5.2025
 * @description
 * DTO used to represent a player's move request including player identifier and number of steps to move.
 */

/**
 * Represents a move request sent by the client to initiate a player's movement on the board.
 *
 * @property playerId The unique identifier or name of the player initiating the move.
 * @property steps The number of steps to move forward on the board.
 */
data class MoveRequest(
    val playerId: String,
    val steps: Int
)
