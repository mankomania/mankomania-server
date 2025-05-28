package at.mankomania.server.controller.dto

data class GameStateDto(
    val players: List<PlayerDto>,
    val board: List<CellDto>
)
