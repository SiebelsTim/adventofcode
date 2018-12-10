package year2018.base

fun main(args: Array<String>) {
    arrayOf(
        year2018.day1.Solution(),
        year2018.day2.Solution(),
        year2018.day3.Solution(),
        year2018.day4.Solution(),
        year2018.day5.Solution(),
        year2018.day6.Solution(),
        year2018.day7.Solution(),
        year2018.day8.Solution(),
        year2018.day9.Solution(),
        year2018.day10.Solution()
    ).forEach {
        it.solveWithMeasurement()
    }
}
