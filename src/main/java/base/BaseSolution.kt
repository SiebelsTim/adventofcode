package base

abstract class BaseSolution<T, T1, T2>(val title: String) {
    fun loadInput(): String {
        val stream = this.javaClass.getResourceAsStream("input.txt")

        return String(stream.readBytes())
    }

    abstract fun parseInput(): T
    abstract fun calculateResult1(): T1
    abstract fun calculateResult2(): T2

    fun solve() {
        println("Result for $title: " + calculateResult1() + " and " + calculateResult2())
    }

    fun solveWithMeasurement() {
        val (firstTime, first) = measureAndResult {
            calculateResult1()
        }
        val (secondTime, second) = measureAndResult {
            calculateResult2()
        }

        println("Result for $title: $first and $second")
        println("Ran in $firstTime ms and $secondTime ms")
    }

    private fun <T> measureAndResult(block: () -> T): Pair<Long, T> {
        val start = System.currentTimeMillis()
        val result = block()
        return Pair(System.currentTimeMillis() - start, result)
    }
}
