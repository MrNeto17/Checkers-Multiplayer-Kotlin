package isel.leic.tds.checkers.board

const val COL_DIM = 8
private const val CODE = 97

class Column private constructor(val symbol: Char) {
    val index = symbol.code - CODE
    companion object {
        val values = Array(COL_DIM) { Column('a'+ it) }
        operator fun invoke(letter:Char) = values[letter.code - CODE]
    }
}

fun Char.toColumnOrNull(): Column? {
    return if(this.code in 'a'.code .. 'h'.code) Column(this)
    else null
}

fun Int.indexToColumn(): Column {
    if((this + CODE).toChar().code in 'a'.code .. 'h'.code)
        return Column((this + CODE).toChar())
    else
        throw IndexOutOfBoundsException()
}


