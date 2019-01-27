package year2017.day6

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, Int>("Day 6") {
    override fun parseInput(): List<Int>  = loadInput()
            .split("\t")
            .map { it.trim().toInt() }

    override fun calculateResult1(): Int {
        var config = parseInput()
        val configurations = mutableSetOf<List<Int>>()
        do {
            configurations += config
            config = reconfigure(config)
        } while(config !in configurations)

        return configurations.size
    }

    override fun calculateResult2(): Int {
        var config = parseInput()
        val configurations = mutableSetOf<List<Int>>()
        do {
            configurations += config
            config = reconfigure(config)
        } while(config !in configurations)

        return configurations.dropWhile { it != config }.size
    }

    private fun reconfigure(originalConfig: List<Int>): List<Int> {
        val config = originalConfig.toMutableList()
        var idx = config.indexOf(config.max()!!)
        var blocks = config[idx]
        config[idx] = 0
        while (blocks > 0) {
            idx = (idx + 1) % config.size
            config[idx]++
            blocks--
        }

        return config
    }
}

fun main() {
    Solution().solveWithMeasurement()
}