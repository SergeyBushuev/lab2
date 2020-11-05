class SquareMatrix(val size: Int, private val elements: (Int, Int) -> Float) {

    operator fun get(row: Int, col: Int): Float {
        require(row in 0..size - 1) { "Строки ${row} вышли за рамки: 0..${size - 1}" }
        require(col in 0..size - 1) { "Столбцы ${col} вышли за рамки: 0..${size - 1}" }
        return elements(row, col)
    }

    val det: Float by lazy {
        if (size == 1) this[0, 0]
        else (0..size - 1).map { item -> this[0, item] * comatrix[0, item] }.sum()
    }

    private val comatrix: SquareMatrix by lazy {
        SquareMatrix(size) { row, col -> cofactor(row, col) }
    }

    private fun cofactor(row: Int, col: Int): Float =
        minor(row, col) * if ((row + col) % 2 == 0) 1f else -1f

    private fun minor(row: Int, col: Int): Float =
        sub(row, col).det

    private fun sub(delRow: Int, delCol: Int) = SquareMatrix(size - 1) { row, col ->
        this[if (row < delRow) row else row + 1, if (col < delCol) col else col + 1]
    }

    val adj: SquareMatrix by lazy { comatrix.transpose() }

    fun transpose(): SquareMatrix = SquareMatrix(size) { row, col -> this[col, row] }

    fun inverse(): SquareMatrix = SquareMatrix(size) { row, col -> adj[row, col] / det }
}
