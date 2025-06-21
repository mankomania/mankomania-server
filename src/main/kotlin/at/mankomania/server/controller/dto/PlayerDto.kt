// src/main/kotlin/at/mankomania/server/controller/dto/PlayerDto.kt
package at.mankomania.server.controller.dto

data class PlayerDto(
    val name: String,
    val position: Int,
    val balance: Int,
    val isTurn: Boolean
)