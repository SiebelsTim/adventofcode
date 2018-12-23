package year2018.day23

import base.BaseSolution
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Coordinate3d(val x: Long, val y: Long, val z: Long) {
    fun distanceTo(other: Coordinate3d): Long = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
    override fun toString(): String = "($x,$y,$z)"

    operator fun minus(other: Coordinate3d) = Coordinate3d(x - other.x, y - other.y, z - other.z)
    operator fun plus(other: Long) = Coordinate3d(x + other, y + other, z + other)
    operator fun minus(other: Long) = Coordinate3d(x - other, y - other, z - other)
    operator fun div(other: Long) = Coordinate3d(x / other, y / other, z / other)
}
class Solution : BaseSolution<List<Solution.Nanobot>, Int, Long>("Day 23") {
    data class Nanobot(val position: Coordinate3d, val radius: Int)

    override fun parseInput(): List<Nanobot> = loadInput().lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val coordStr = it.substring("pos=<".length, it.indexOf('>'))
            val (x, y, z) = coordStr.split(',').map(String::toLong)
            val radius = it.substringAfter("r=")
            Nanobot(Coordinate3d(x, y, z), radius.toInt())
        }.toList()

    override fun calculateResult1(): Int {
        val input = parseInput()
        val strongest = input.maxBy { it.radius }!!

        return input.inRange(strongest.position)
    }

    override fun calculateResult2(): Long {
        val input = parseInput()
        var min = input.map { it.position }.fold(input.first().position) { acc, it ->
            Coordinate3d(min(acc.x, it.x), min(acc.y, it.y), min(acc.z, it.z))
        }
        var max = input.map { it.position }.fold(input.first().position) { acc, it ->
            Coordinate3d(max(acc.x, it.x), max(acc.y, it.y), max(acc.z, it.z))
        }

        var best = input.map { it.position }.avg() // Start with the middle position
        var bestValue = input.inRange(best)

        var dist = 1L
        while (dist < max.x - min.x)  { // get power of 2 that is smaller than x dist
            dist *= 2
        }

        while (dist > 1) {
            for (x in min.x..max.x + 1 step dist) {
                for (y in min.y..max.y + 1 step dist) {
                    for (z in min.z..max.z + 1 step dist) {
                        val pos = Coordinate3d(x, y, z)
                        val count = input.inRange(pos)
                        if (count > bestValue) {
//                            println("$bestValue -> $count")
                            bestValue = count
                            best = pos
                        } else {
                            val origin = Coordinate3d(0, 0, 0)
                            if (count == bestValue && origin.distanceTo(pos) < origin.distanceTo(best)) {
                                best = pos
                            }
                        }
                    }
                }
            }

            min = best - dist
            max = best + dist
            dist /= 2
        }

        return Coordinate3d(0L,0L,0L).distanceTo(best)
    }

    private fun List<Nanobot>.inRange(coordinate: Coordinate3d): Int {
        return this.count {
            it.position.distanceTo(coordinate) <= it.radius
        }
    }

    private fun List<Coordinate3d>.avg(): Coordinate3d {
        val x = map { it.x }.average().toLong()
        val y = map { it.y }.average().toLong()
        val z = map { it.z }.average().toLong()

        return Coordinate3d(x, y, z)
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
