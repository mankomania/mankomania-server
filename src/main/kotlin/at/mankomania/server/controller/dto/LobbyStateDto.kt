/**
 * @file LobbyStateDto.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Represents the current state of a game lobby, including which players have joined and whether the game can start.
 *
 * @property gameId The identifier of the game lobby.
 * @property players List of player names currently joined.
 * @property canStart Flag indicating if the lobby meets the criteria to start the game (2–4 players).
 */
package at.mankomania.server.controller.dto

data class LobbyStateDto(
    val gameId: String,
    val players: List<String>,
    val canStart: Boolean
)