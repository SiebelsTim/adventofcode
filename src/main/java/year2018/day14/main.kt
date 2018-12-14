package year2018.day14

import base.BaseSolution

class Solution : BaseSolution<Int, String, Int>("Day 14") {
    class Scoreboard() {
        private val _scoreboard = ArrayList<Int>(30_000_000).apply {
            // setting initial capacity gains performance
            add(3)
            add(7)
        }
        val scoreboard: List<Int> get() = _scoreboard

        private var elfPositions = listOf(0, 1)

        fun step(n: Int): List<Int> {
            while (scoreboard.size < n) {
                val combined = elfPositions.sumBy { scoreboard[it] }
                val newScores = if (combined >= 10) {
                    listOf(combined / 10, combined % 10)
                } else {
                    listOf(combined)
                }

                _scoreboard += newScores

                elfPositions = elfPositions.map {
                    (it + 1 + scoreboard[it]) % scoreboard.size
                }
            }

            return scoreboard
        }

        fun last(n: Int) = scoreboard.takeLast(n)
    }

    override fun parseInput(): Int = loadInput().trim().toInt()

    override fun calculateResult1(): String {
        val scoreboard = Scoreboard().step(parseInput()+10)

        return scoreboard.takeLast(10).joinToString("")
    }

    override fun calculateResult2(): Int {
        val input = parseInput().toString()
        val digitCount = input.length
        val scoreboard = Scoreboard()

        while (!scoreboard.last(digitCount+1).joinToString("").contains(input)) {
            scoreboard.step(scoreboard.scoreboard.size + 1)
        }

        return scoreboard.scoreboard.size - digitCount - if (scoreboard.last(digitCount).joinToString("") == input) {
            0
        } else {
            1
        }
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
