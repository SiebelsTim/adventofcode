package year2018.day7

import base.BaseSolution
import kotlin.math.max

class Solution : BaseSolution<List<Solution.Message>, String, Int>("Day 7") {
    data class Message(val id: Char, val dependent: Char) {
        companion object {
            fun of(line: String): Message {
                val (dependent, id) = Regex("Step ([A-Z]) must be finished before step ([A-Z]) can begin.").find(line)!!.destructured
                return Message(id[0], dependent[0])
            }
        }
    }

    data class Step(val id: Char, val dependencies: MutableList<Step> = mutableListOf()) {
        val duration: Int get() = 60 + (id - 'A' + 1)
        fun canCompute(ready: List<Step>): Boolean {
            return dependencies.all { ready.contains(it) }
        }
    }

    class Worker(var remainingTime: Int, var currentStep: Step? = null)

    override fun parseInput(): List<Message> = loadInput().lines()
        .filter { it.isNotBlank() }
        .map { Message.of(it) }

    override fun calculateResult1(): String {
        val tree = computeTree().toMutableList()
        val ready = mutableListOf<Step>()
        val canStart = mutableListOf<Step>()

        while (tree.isNotEmpty()) {
            val iterator = tree.iterator()
            while (iterator.hasNext()) {
                val step = iterator.next()
                if (step.canCompute(ready)) {
                    canStart += step
                    iterator.remove()
                }
            }

            canStart.sortBy { it.id }
            ready += canStart.first()
            canStart.removeAt(0)
        }

        return ready.map { it.id }.joinToString("")
    }

    override fun calculateResult2(): Int {
        val tree = computeTree().toMutableList()
        val stepCount = tree.size
        val ready = mutableListOf<Step>()
        val canStart = mutableListOf<Step>()

        val workers = Array(5) { Worker(0) }

        var time = 0
        while (ready.size < stepCount) {
            for (worker in workers.filter { it.remainingTime == 0 }) {
                worker.currentStep?.let {
                    ready += it
                    worker.currentStep = null
                }
            }

            val iterator = tree.iterator()
            while (iterator.hasNext()) {
                val step = iterator.next()
                if (step.canCompute(ready)) {
                    canStart += step
                    iterator.remove()
                }
            }

            for (worker in workers) {
                worker.remainingTime = max(worker.remainingTime-1, 0)
            }

            canStart.sortBy { it.id }
            for (worker in workers.filter { it.currentStep == null }) {
                canStart.firstOrNull()?.let { first ->
                    worker.currentStep = first
                    canStart.removeAt(0)
                    worker.remainingTime = first.duration - 1
                }
            }

            ++time
        }

        return time-1
    }

    private fun computeTree(): List<Step> {
        val input = parseInput()
            .groupBy { it.id }
        val steps = mutableMapOf<Char, Step>()
        for ((id, dependencies) in input) {
            val step = steps.computeIfAbsent(id) { Step(it) }
            dependencies.map { it.dependent }.map { depId ->
                steps.computeIfAbsent(depId) { Step(it) }
            }.forEach { dependent ->
                step.dependencies += dependent
            }
        }

        return steps.values.toList()
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
