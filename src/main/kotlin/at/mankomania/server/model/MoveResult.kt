package at.mankomania.server.model

data class MoveResult(
    val newPosition: Int,
    val oldPosition: Int,
    val fieldType: String,
    val fieldDescription: String,
    val playersOnField: List<String>
)
