
/**
 * @file GameStartedDto.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Response DTO sent when a game session successfully starts, containing starting positions for each player.
 *
 * @property gameId The identifier of the started game.
 * @property startPositions The list of starting field indices for each player in join order.
 * @property firstPlayerIndex Index (0-based) of the player who begins
 */
package at.mankomania.server.controller.dto

data class GameStartedDto(
    val gameId: String,
    val startPositions: List<Int>,
    val firstPlayerIndex: Int
)