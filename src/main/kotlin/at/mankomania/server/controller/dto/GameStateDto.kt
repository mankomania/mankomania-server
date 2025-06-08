package at.mankomania.server.controller.dto

import at.mankomania.server.model.BoardCell
import at.mankomania.server.model.Player

data class GameStateDto(
    val players: List<Player>,
    val board: List<BoardCell>,
    val currentPlayer: String
)
