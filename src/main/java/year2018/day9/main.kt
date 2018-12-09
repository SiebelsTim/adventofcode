package year2018.day9

import year2018.base.BaseSolution
import kotlin.math.max

typealias PlayerCount = Int
typealias Points = Int
class Solution : BaseSolution<Pair<PlayerCount, Points>, Int, Int>("Day 9") {
    override fun parseInput(): Pair<PlayerCount, Points> = loadInput().let {
        val (players, points) = Regex("""(\d+) players; last marble is worth (\d+) points""").find(it)?.destructured ?: throw IllegalArgumentException("Regex didnt match")
        Pair(players.toInt(), points.toInt())
    }

    override fun calculateResult1(): Int {
        val (players, lastPoints) = parseInput()
//        val (players, lastPoints) = arrayOf(10, 1618)
        val nextPlayer = generateSequence(1) { ((it + 1) % players) }.iterator()
        val marbles = generateSequence(2) { i -> i + 1 }.take(lastPoints)
        val board = mutableListOf<Int>(0, 1)
        var currentIdx = 1
        val playerScores = IntArray(players)

        for (currentMarble in marbles) {
            val currentPlayer = nextPlayer.next()
            if (currentMarble % 23 == 0) {
                currentIdx = (currentIdx - 7).let {
                    if (it < 0) {
                        board.size + it
                    } else {
                        it
                    }
                }
                playerScores[currentPlayer] += board.removeAt(currentIdx) + currentMarble
            } else {
                currentIdx = (currentIdx + 2) % (board.size)
                if (currentIdx == 0) {
                    currentIdx = board.size
                    board.add(currentMarble)
                } else {
                    board.add(currentIdx, currentMarble)
                }
            }
        }

        return playerScores.max()!!
    }

    override fun calculateResult2(): Int {
        return -1
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
