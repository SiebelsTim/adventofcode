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
        year2018.day10.Solution(),
        year2018.day11.Solution(),
        year2018.day12.Solution(),
        year2018.day13.Solution(),
        year2018.day14.Solution(),
        year2018.day15.Solution(),
        year2018.day16.Solution(),
        year2018.day17.Solution(),
        year2018.day18.Solution(),
        year2018.day19.Solution(),
        year2018.day20.Solution(),
        year2018.day21.Solution(),
        year2018.day22.Solution(),
        year2018.day23.Solution(),
        year2018.day24.Solution(),
        year2018.day25.Solution()
    ).forEach {
        it.solveWithMeasurement()
    }
}
