package at.mankomania.server.model
import at.mankomania.server.controller.dto.PlayerDto
import at.mankomania.server.controller.dto.CellDto

class MapperExtensions {
    fun Player.toDto(): PlayerDto {
        return PlayerDto(name, position)
    }

    fun BoardCell.toDto(): CellDto {
        return CellDto(index, hasBranch)
    }
}