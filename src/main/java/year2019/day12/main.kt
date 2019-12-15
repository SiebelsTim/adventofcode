package year2019.day12

import base.BaseSolution
import base.Coordinate3d
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Solution : BaseSolution<List<Solution.Moon>, Int, Long>("Day 12") {
    data class Moon(val position: Coordinate3d, val velocity: Coordinate3d = Coordinate3d(0, 0, 0)) {
        val potentialEnergy: Int
            get() = abs(position.x) + abs(position.y) + abs(position.z)

        val kineticEnegery: Int
            get() = abs(velocity.x) + abs(velocity.y) + abs(velocity.z)

        val totalEnegery: Int
            get() = potentialEnergy * kineticEnegery

        fun move(): Moon = copy(position = position + velocity)
    }

    override fun parseInput(): List<Moon> = loadInput().lines().mapIndexed { idx, it ->
        val (x, y, z) = Regex("""<x=(-?[0-9]+), y=(-?[0-9]+), z=(-?[0-9]+)>""").find(it)!!.destructured

        Moon(Coordinate3d(x.toInt(), y.toInt(), z.toInt()))
    }

    override fun calculateResult1(): Int {
        var moons = parseInput()
        repeat(1000) { i ->
            moons = moons.updateGravity()
            moons = moons.map { it.move() }
        }
        return moons.sumBy { it.totalEnegery }
    }

    override fun calculateResult2(): Long {
        var moons = parseInput()
        val initial = moons
        var xCycle = 0
        var yCycle = 0
        var zCycle = 0
        var i = 0
        while (xCycle == 0 || yCycle == 0 || zCycle == 0) {
            ++i
            moons = moons.updateGravity()
            moons = moons.map { it.move() }
            if (xCycle == 0 && moons.map { it.velocity.x to it.position.x } == initial.map { it.velocity.x to it.position.x }) {
                xCycle = i
            }
            if (yCycle == 0 && moons.map { it.velocity.y to it.position.y } == initial.map { it.velocity.y to it.position.y }) {
                yCycle = i
            }

            if (zCycle == 0 && moons.map { it.velocity.z to it.position.z } == initial.map { it.velocity.z to it.position.z }) {
                zCycle = i
            }
        }

        return lcm(xCycle.toLong(), yCycle.toLong(), zCycle.toLong())
    }

    private fun lcm(vararg numbers: Long) = numbers.drop(1).fold(numbers.first()) { a, b ->
        lcm(a, b)
    }
    private fun lcm(a: Long, b: Long): Long = a*b / gcd(a, b)

    private fun gcd(a: Long, b: Long): Long {
        var greater = min(a, b)
        var smaller = max(a, b)
        do {
            val remainder = greater % smaller
            val newNumber = greater / smaller
            greater = smaller
            smaller = remainder
        } while (remainder > 0)

        return greater
    }

    private fun List<Moon>.updateGravity(): List<Moon> {
        val differences = mutableMapOf<Moon, List<Coordinate3d>>()
        for (i in 0 until size) {
            for (j in i+1 until size) {
                val (diff1, diff2) = updateGravity(this[i], this[j])
                val previous1 = differences.computeIfAbsent(this[i]) { emptyList() }
                val previous2 = differences.computeIfAbsent(this[j]) { emptyList() }
                differences[this[i]] = previous1 + diff1
                differences[this[j]] = previous2 + diff2
            }
        }

        return map {
            val vel = differences[it]!!.fold(Coordinate3d(0, 0, 0)) { acc, it -> acc + it }
            it.copy(velocity = it.velocity + vel)
        }
    }

    private fun updateGravity(moon1: Moon, moon2: Moon): Pair<Coordinate3d, Coordinate3d> {
        val newX = when {
            moon1.position.x < moon2.position.x -> +1
            moon1.position.x > moon2.position.x -> -1
            else -> 0
        }
        val newY = when {
            moon1.position.y < moon2.position.y -> +1
            moon1.position.y > moon2.position.y -> -1
            else -> 0
        }
        val newZ = when {
            moon1.position.z < moon2.position.z -> +1
            moon1.position.z > moon2.position.z -> -1
            else -> 0
        }
        val newCoordinate = Coordinate3d(newX, newY, newZ)

        return newCoordinate to -newCoordinate
    }
}

fun main() {
    Solution().solveWithMeasurement()
}