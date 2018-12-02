package day2

import base.BaseSolution
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class Solution : BaseSolution<List<String>, Int, String>("Day 2") {
    override fun parseInput(): List<String> = loadInput().split("\n").filter { it.isNotBlank() }

    override fun calculateResult1(): Int {
        var result = Counter.Result(0, 0)
        for (str in parseInput()) {
            result += Counter(str).calculate()
        }

        return result.twos * result.threes
    }

    override fun calculateResult2(): String {
        val input = parseInput()
        for (i in 0 until input.size) {
            for (j in i+1 until input.size) {
                val differencer = Differencer(input[i], input[j])
                val differenceCount = differencer.differenceCount()
                if (differenceCount.count == 1) {
                    return input[i].filterIndexed { index, _ -> index != differenceCount.positions[0] }
                }
            }
        }

        throw IllegalStateException("No result Found")
    }

    class Differencer(val a: String, val b: String) {
        data class Result(val count: Int, val positions: List<Int>)
        fun differenceCount(): Result {
            if (a.length != b.length) {
                throw IllegalArgumentException("String are not the same length")
            }

            val positions = mutableListOf<Int>()
            var counter = 0
            for (i in 0 until a.length) {
                if (a[i] != b[i]) {
                    ++counter
                    positions += i
                }
            }

            return Result(counter, positions)
        }
    }

    class Counter(val input: String) {
        data class Result(val twos: Int, val threes: Int) {
            operator fun plus(other: Result) = Result(twos + other.twos, threes + other.threes)
        }
        fun calculate(): Result {
            val map = mutableMapOf<Char, Int>()
            val input = input.toCharArray()
            for (c in input) {
                map[c] = (map[c] ?: 0) + 1
            }

            val results = map.filter { it.value >= 2 }
                .map { if (it.value == 2) '2' else '3' }

            return Result(if (results.contains('2')) 1 else 0, if (results.contains('3')) 1 else 0)
        }
    }
}


fun main() {
    Solution().solve()
}
