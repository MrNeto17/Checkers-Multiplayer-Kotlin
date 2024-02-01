package isel.leic.tds.checkers.board

const val BOARD_DIM = 8

class Square private constructor(val row: Row, val column: Column) {
    val playable: Boolean = (((ROW_DIM-1-row.index) + column.index) % 2) == 0
    companion object {
        val values = Array(ROW_DIM * COL_DIM) {
            val square = Square((it / 8).indexToRow(), (it % 8).indexToColumn() )
            square
        }
        operator fun invoke(row: Row, column: Column): Square {
            return values[(ROW_DIM -1-row.index) * ROW_DIM + column.index]
        }
        operator fun invoke(row: Int, column: Int) = values[(ROW_DIM -1-row) * ROW_DIM + column]
    }
    override fun toString() = ""+ this.row.number + this.column.symbol
}


fun String.toSquareOrNull(): Square? {
    if (this.length > 2 || this[0] !in '1'..'8' ) return null
    val row = this[0].digitToInt().toRowOrNull()
    val col = this[1].toColumnOrNull()
    return if (row != null && col != null ) Square(row,col) else null
}

