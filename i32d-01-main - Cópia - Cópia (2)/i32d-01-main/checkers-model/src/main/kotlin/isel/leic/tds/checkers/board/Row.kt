package isel.leic.tds.checkers.board

const val ROW_DIM = 8

class Row(val number: Int) {
    companion object {
        val values = Array(ROW_DIM) { ROW_DIM - it }
        val Int.number: Int
            get() = this
    }
    val index = number - 1
}

fun Int.toRowOrNull(): Row? {
    return if(this in 1.. 8) Row(this)
    else null
}

fun Int.indexToRow(): Row {
    if(this !in 0.. 7) throw IndexOutOfBoundsException()
    return Row(ROW_DIM - this)
}
