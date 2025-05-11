package at.mankomania.server.controller.dto

/**
 * @file DiceMoveResultDto.kt
 * @author eles17
 * @since 11.5.2025
 * @description
 * Combines the result of a Dice roll and resulting move for the player.
 * Used in WebSocket communication to return all state changes after a roll.
 */

data class DiceMoveResultDto(
    val playerId: String,
    val die1: Int,
    val die2: Int,
    val sum: Int,
    val fieldIndex: Int,
    val fieldType: String,
    val fieldDescription: String,
    val playersOnField: List<String>
)