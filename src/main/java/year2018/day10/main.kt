package year2018.day10

import year2018.base.BaseSolution

class Solution : BaseSolution<List<Solution.Light>, String, Int>("Day 10") {
    data class Light(val position: Pair<Int, Int>, val velocity: Pair<Int, Int>) {
        val x: Int get() = position.first
        val y: Int get() = position.second

        fun move(steps: Int): Light = Light(Pair(x + velocity.first * steps, y + velocity.second * steps), velocity)
    }
    
    override fun parseInput(): List<Light> = loadInput()
        .split("\n")
        .filter { it.isNotBlank() }
        .map {
            val (x, y, vx, vy) = Regex("""position=<([ 0-9\-]+), ([ 0-9\-]+)> velocity=<([ 0-9\-]+), ([ 0-9\-]+)>""")
                .find(it)?.destructured ?: throw IllegalArgumentException("Regex did not match")

            Light(x.trim().toInt() to y.trim().toInt(), vx.trim().toInt() to vy.trim().toInt())
        }

    override fun calculateResult1(): String {
        return arrangeLights().first
    }

    override fun calculateResult2(): Int {
        return arrangeLights().second
    }

    private fun arrangeLights(): Pair<String, Int> {
        var lights = parseInput()
        var lastDistance = Int.MAX_VALUE
        var time = 0
        while (true) {
            lights = lights.map { it.move(1) }
            val minX = lights.minBy { it.x }!!.x
            val minY = lights.minBy { it.y }!!.y
            val maxX = lights.maxBy { it.x }!!.x
            val maxY = lights.maxBy { it.y }!!.y
            val distance = (maxX - minX) + (maxY - minY)

            if (distance > lastDistance) {
                break
            }

            lastDistance = distance
            time++
        }

        lights = parseInput().map { it.move(time) }
        val minX = lights.minBy { it.x }!!.x
        val minY = lights.minBy { it.y }!!.y

        // move all lights to start at 0
        lights = lights.map { Light(Pair(it.x - minX, it.y - minY), it.velocity) }

        val maxX = lights.maxBy { it.x }!!.x
        val maxY = lights.maxBy { it.y }!!.y
        val str = StringBuilder("\n")
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                str.append(if (lights.find { it.x == x && it.y == y } != null) {
                    "X"
                } else {
                    "."
                })
            }
            str.append("\n")
        }

        return str.toString() to time
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
