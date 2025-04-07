package at.mankomania.server.model

/**
 * Represents a player in the game.
 *
 * @property id Unique identifier or name of the player.
 * @property position The current position of the player on the board.
 * @property balance The player's current money.
 */
data class Player(
    val id: String,
    var position: Int = 0,
    var balance: Int = 0
) {
    fun move(steps: Int, board: Board): Boolean {
        require(steps >= 0) { "Steps must be non-negative." }

        position = (position + steps) % board.size

        val currentField = board.getField(position)
        return if (currentField.hasBranch) {
            chooseBranch(currentField.branchOptions)
            true
        } else {
            false
        }
    }

    fun hasBranch(board: Board): Boolean {
        return board.getField(position).hasBranch
    }

    fun chooseBranch(branchOptions: List<Int>) {
        val chosen = branchOptions.firstOrNull()
        if (chosen != null) {
            println("Branching: Player '$id' chooses to go to field $chosen.")
            position = chosen
        } else {
            println("Branching: No branch options available for Player '$id'.")
        }
    }

    fun getCurrentPosition(): Int {
        return position
    }
}
