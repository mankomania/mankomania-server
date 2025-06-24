package at.mankomania.server.controller.dto

import at.mankomania.server.model.BoardCell

data class GameStateDto(
    val players: List<PlayerDto>,
    val board: List<BoardCell>,
    val currentTurnPlayerName: String = ""
)
