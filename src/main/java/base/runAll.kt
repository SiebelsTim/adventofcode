package base

fun main(args: Array<String>) {
    arrayOf(day1.Solution(), day2.Solution(), day3.Solution()).forEach {
        it.solveWithMeasurement()
    }
}
