package year2018.day5

import base.BaseSolution

class Solution : BaseSolution<MutableList<Char>, Int, Int>("Day 5") {
    override fun parseInput(): MutableList<Char> = loadInput().trim().toMutableList()
//    override fun parseInput(): MutableList<Char> = "AabAcCaCBAcCcaDA".toMutableList()

    override fun calculateResult1(): Int {
        return exercise1(parseInput())
    }

    override fun calculateResult2(): Int {
        val results = mutableListOf<Int>()
        for (c in 'a'..'z') {
            val input = parseInput().filter {
                it != c && it != c.toUpperCase()
            } as MutableList<Char>
            results += exercise1(input)
        }

        return results.min()!!
    }

    private fun exercise1(input: MutableList<Char>): Int {
        val iterator = input.listIterator()
        while (iterator.hasNext()) {
            val first = iterator.next()
            if (!iterator.hasNext()) { // Check hasNext twice
                break
            }
            val second = iterator.next()
            if (first.toLowerCase() == second.toLowerCase() && first != second) {
                iterator.apply {
                    remove()
                    previous()
                    remove()
                }
            }
            if (iterator.hasPrevious()) {
                iterator.previous()
            }
        }

        return input.size
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
