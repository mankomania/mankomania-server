/**
 * @file CellState.kt
 * @author Angela Drucks
 * @since 2025-04-02
 * @description Represents the state of a board cell, which can either be FREE or OCCUPIED.
 * This enum is used to track the current status of a board cell, indicating if it is available for a player to land on or already occupied.
 */

package at.mankomania.server.model

/**
 * Represents the state of a board cell.
 */

enum class CellState {
    FREE,
    OCCUPIED
}