package year2018.day9

import year2018.base.BaseSolution

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

    private fun solve(players: PlayerCount, lastPoints: Points): Long {
        val nextPlayer = generateSequence(1) { ((it + 1) % players) }.iterator()
        val marbles = generateSequence(2L) { i -> i + 1 }.take(lastPoints)
        val board = Queue(0L)
        var current = board.add(1)
        val playerScores = LongArray(players)

        for (currentMarble in marbles) {
            val currentPlayer = nextPlayer.next()
            if (currentMarble % 23 == 0L) {
                for (i in 1..7) {
                    current = current.prev
                }
                playerScores[currentPlayer] += current.value + currentMarble
                board.remove(current)
                current = current.next
            } else {
                current = board.addAfter(current.next, currentMarble)
            }
        }

        return playerScores.max()!!
    }
}

class Queue<T>(initial: T) {
    class Node<T>(var value: T) {
        var next: Node<T> = this
        var prev: Node<T> = this
    }

    var start: Node<T> = Node<T>(initial)

    fun addAfter(before: Node<T>, value: T): Node<T> {
        val node = Node<T>(value)
        before.next.prev = node
        node.next = before.next
        before.next = node
        node.prev = before

        return node
    }

    fun add(value: T): Node<T> {
        return addAfter(start.prev, value)
    }

    fun remove(node: Node<T>) {
        node.prev.next = node.next
        node.next.prev = node.prev
    }

    fun toList(): List<T> {
        val ret = mutableListOf<T>()
        var current = start
        do {
            ret += current.value
            current = current.next
        } while (current != start)

        return ret
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
