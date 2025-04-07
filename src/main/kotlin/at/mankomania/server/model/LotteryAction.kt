package at.mankomania.server.model

import java.time.LocalDateTime

class LotteryAction {
    private var pool: Int = 0
    var currentAmount: Int = 0
    val transactions: MutableList<LotteryTransaction> = mutableListOf()

    data class LotteryTransaction(
        val playerId: String,
        val amount: Int,
        val reason: String,
        val timestamp: LocalDateTime = LocalDateTime.now()
    )

    fun addToPool(amount: Int) {
        require(amount > 0)
        pool += amount
    }

    fun takeFromPool(): Int {
        val amount = pool
        pool = 0
        return amount
    }

    fun getPoolAmount(): Int = pool

    fun isEmpty(): Boolean = pool == 0
}