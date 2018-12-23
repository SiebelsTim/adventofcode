package year2018.day23

import base.BaseSolution
import kotlin.math.abs

data class Coordinate3d(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: Coordinate3d): Int = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    override fun toString(): String = "($x,$y,$z)"
}
class Solution : BaseSolution<List<Solution.Nanobot>, Int, Int>("Day 23") {
    data class Nanobot(val position: Coordinate3d, val radius: Int)

    override fun parseInput(): List<Nanobot> = loadInput().lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val coordStr = it.substring("pos=<".length, it.indexOf('>'))
            val (x, y, z) = coordStr.split(',').map(String::toInt)
            val radius = it.substringAfter("r=")
            Nanobot(Coordinate3d(x, y, z), radius.toInt())
        }.toList()

    override fun calculateResult1(): Int {
        val input = parseInput()
        val strongest = input.maxBy { it.radius }!!

        return input.count {
            strongest.position.distanceTo(it.position) <= strongest.radius
        }
    }

    override fun calculateResult2(): Int {
        return -1
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
