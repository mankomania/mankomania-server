/**
 * @file JoinDto.kt
 * @author Angela Drucks
 * @since 2025-05-07
 * @description Data Transfer Object for joining a game lobby. Contains the name of the player who wants to join.
 */

package at.mankomania.server.controller.dto

data class JoinDto(val playerName: String)