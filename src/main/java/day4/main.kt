package day4

import base.BaseSolution

typealias Minute = Int
typealias Count = Int
class Solution : BaseSolution<List<Solution.Event>, Int, Int>("Day 4") {
    data class Event(val date: Date, val time: Time, val message: Message) {
        companion object {
            fun fromString(str: String): Event {
                Regex("""\[(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2})\] (.*)""").find(str)?.groups?.let { groups ->
                    val parts = groups.drop(1).mapNotNull { it?.value }
                    val numbers = parts.dropLast(1).map { it.toInt() }
                    val messageContent = parts.last()
                    if (parts.size != 6) {
                        throw IllegalArgumentException("Illegal part size")
                    }

                    val message = when(messageContent) {
                        "wakes up" -> Message.WakeUp
                        "falls asleep" -> Message.Sleep
                        else -> Message.Begin(Regex("(Guard #| begins shift)").replace(messageContent, "").toInt())
                    }
                    return Event(
                        Date(numbers[0], numbers[1], numbers[2]),
                        Time(numbers[3], numbers[4]),
                        message
                    )
                } ?: throw IllegalArgumentException("Regex did not match")
            }
        }
    }
    data class Date(val year: Int, val month: Int, val day: Int)
    data class Time(val hours: Int, val minutes: Int)
    sealed class Message() {
        object WakeUp : Message()
        object Sleep : Message()
        class Begin(val guard: Int): Message()
    }

    data class Guard(val id: Int, val sleepTimes: MutableList<IntRange> = mutableListOf()) {
        fun howMuchSleep(): Int = sleepTimes.map { range -> range.endInclusive - range.start }.sum()

        fun getMostSleepyMinute(): Minute? = getMostSleepyMinuteWithCount()?.first

        fun getMostSleepyMinuteWithCount(): Pair<Minute, Count>? = sleepTimes
            .flatMap { it.toList() }
            .groupingBy { it }
            .eachCount()
            .maxBy { it.value }
            ?.toPair()
    }

    override fun parseInput(): List<Event> = loadInput()
        .split("\n")
        .filter { it.isNotBlank() }
        .map { Event.fromString(it) }

    override fun calculateResult1(): Int {
        val input = parseInput().sortedBy {
            "" + it.date.year + "%2d".format(it.date.month) + "%2d".format(it.date.day) +
                    "%2d".format(it.time.hours) + "%2d".format(it.time.minutes)
        }

        val guards = parseGuards(input)
        val sleeper = guards.maxBy { it.howMuchSleep() }!!
        val minute = sleeper.getMostSleepyMinute()!!

        return sleeper.id * minute
    }

    override fun calculateResult2(): Int {
        val input = parseInput().sortedBy {
            "" + it.date.year + "%2d".format(it.date.month) + "%2d".format(it.date.day) +
                    "%2d".format(it.time.hours) + "%2d".format(it.time.minutes)
        }

        val guards = parseGuards(input)
        val sleeper = guards
            .associate { Pair(it, it.getMostSleepyMinuteWithCount()) }
            .maxBy { it.value?.second ?: 0 }!!
        val minute = sleeper.value!!.first

        return sleeper.key.id * minute
    }

    private fun parseGuards(input: List<Event>): List<Guard> {
        val guards = mutableMapOf<Int, Guard>()
        lateinit var currentGuard: Guard
        var sleepStart = -1
        for (event in input) {
            val msg = event.message
            when (msg) {
                is Message.Begin -> {
                    currentGuard = guards[msg.guard] ?: Guard(msg.guard)
                    guards[msg.guard] = currentGuard
                }
                Message.Sleep -> sleepStart = event.time.minutes
                Message.WakeUp -> {
                    currentGuard.sleepTimes.add(sleepStart until event.time.minutes)
                }
            }
        }

        return guards.map { it.value }
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
