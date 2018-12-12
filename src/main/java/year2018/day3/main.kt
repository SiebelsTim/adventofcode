package year2018.day3

import base.BaseSolution
import java.lang.IllegalStateException

class Solution : BaseSolution<List<Solution.Rectangle>, Int, Int>("Day 3") {
    data class Rectangle(val id: Int, val left: Int, val top: Int, val width: Int, val height: Int) {
        val x2: Int get() = left + width - 1
        val y2: Int get() = top + height - 1
        val x: Int get() = left
        val y: Int get() = top
        companion object {
            fun fromString(str: String): Rectangle {
                val result = Regex("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)").find(str)
                val items = result?.groups?.mapNotNull { it?.value }?.drop(1)?.map { it.toInt() }
                if (items == null || items.size != 5) {
                    throw IllegalArgumentException()
                }

                return Rectangle(items[0], items[1], items[2], items[3], items[4])
            }
        }

        fun intersects(other: Rectangle): Boolean {
            if (other.x > x2 || other.x2 < x) {
                return false
            }

            if (other.y > y2 || other.y2 < y) {
                return false
            }

            return true
        }
    }

    override fun parseInput(): List<Rectangle> = loadInput()
        .split("\n")
        .filter { it.isNotBlank() }
        .map { Rectangle.fromString(it) }

    override fun calculateResult1(): Int {
        val input = parseInput()
        val grid = Array(1000) { IntArray(1000) }
        for (rectangle in input) {
            val startX = rectangle.left
            val stopX = rectangle.left + rectangle.width - 1
            val startY = rectangle.top
            val stopY = rectangle.top + rectangle.height - 1
            for (x in startX..stopX) {
                for (y in startY..stopY) {
                    if (grid[x][y] != 0) {
                        grid[x][y] = -1
                    } else {
                        grid[x][y] = rectangle.id
                    }
                }
            }
        }

        var ret = 0

        for (g in grid) {
            for (entry in g) {
                if (entry == -1) {
                    ret += 1
                }
            }
        }

        return ret
    }

    override fun calculateResult2(): Int {
        val input = parseInput()

        for (i in 0 until input.size) {
            var intersected = false
            for (j in 0 until input.size) {
                if (i == j) {
                    continue
                }
                val one = input[i]
                val two = input[j]
                if (one.intersects(two)) {
                    intersected = true
                    break
                }
            }
            if (!intersected) {
                return input[i].id
            }
        }

        throw IllegalStateException("No one found")
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
