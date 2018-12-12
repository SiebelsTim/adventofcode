package year2018.day9

import base.BaseSolution

typealias PlayerCount = Int
typealias Points = Int
class Solution : BaseSolution<Pair<PlayerCount, Points>, Long, Long>("Day 9") {
    override fun parseInput(): Pair<PlayerCount, Points> = loadInput().let {
        val (players, points) = Regex("""(\d+) players; last marble is worth (\d+) points""").find(it)?.destructured ?: throw IllegalArgumentException("Regex didnt match")
        Pair(players.toInt(), points.toInt())
    }

    override fun calculateResult1(): Long {
        val (players, lastPoints) = parseInput()
        return solve(players, lastPoints)
    }

    override fun calculateResult2(): Long {
        val (players, lastPoints) = parseInput()
        return solve(players, lastPoints * 100)
    }

    private fun solve(players: Int, lastPoints: Int): Long {
        val nextPlayer = generateSequence(1) { ((it + 1) % players) }.iterator()
        val marbles = generateSequence(1L) { i -> i + 1 }.take(lastPoints)
        var current = Node(0L)
        val playerScores = LongArray(players)

        for (currentMarble in marbles) {
            val currentPlayer = nextPlayer.next()
            if (currentMarble % 23 == 0L) {
                repeat(7) {
                    current = current.prev
                }
                playerScores[currentPlayer] += current.value + currentMarble
                current.remove()
                current = current.next
            } else {
                current = current.next.addAfter(currentMarble)
            }
        }

        return playerScores.max()!!
    }
}
class Node<T>(var value: T) {
    var next: Node<T> = this
    var prev: Node<T> = this

    fun addAfter(value: T): Node<T> {
        val node = Node(value)
        this.next.prev = node
        node.next = this.next
        this.next = node
        node.prev = this

        return node
    }

    fun remove() {
        this.prev.next = this.next
        this.next.prev = this.prev
    }

    fun toList(): List<T> {
        val ret = mutableListOf<T>()
        var current = this
        do {
            ret += current.value
            current = current.next
        } while (current != this)

        return ret
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
