/**
 * @file StartDto.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Data Transfer Object for starting a game session. Contains the desired board size.
 */
package at.mankomania.server.controller.dto

data class StartDto(
    val boardSize: Int
)