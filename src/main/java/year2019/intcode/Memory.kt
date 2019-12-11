package year2019.intcode

class Memory(private val memory: MutableList<MemoryCell>) : MutableList<MemoryCell> by memory {
    override operator fun get(index: Int): MemoryCell {
        autoExpand(index)

        return memory[index]
    }

    operator fun get(index: MemoryCell) = get(index.toInt())

    override operator fun set(index: Int, element: MemoryCell): MemoryCell {
        return this[index].also {
            memory[index] = element
        }
    }

    operator fun set(index: MemoryCell, element: MemoryCell) = set(index.toInt(), element)

    private fun autoExpand(index: Int) {
        if (size <= index) {
            memory += List(index - size + 1) { 0L }
        }
    }
}


fun List<MemoryCell>.toMemory() = Memory(this.toMutableList())

