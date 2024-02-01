package isel.leic.tds.checkers.board

import kotlin.math.abs


sealed class PlayReturn {
    object PlayAgain : PlayReturn()
    object ValidPlay : PlayReturn()
    object InvalidPlay : PlayReturn()
    object MandatoryPlay: PlayReturn()
    object YouWon: PlayReturn()
}

class Board{
    val boardArr: Array<Array<Representation>>

    constructor(){
        boardArr = Array(8){ Array(8){Representation.NonPlayable} }
        Square.values.forEach {
            val idxX = ROW_DIM - 1 - it.row.index
            val idxY = it.column.index
            val isPlayable = (idxX % 2 == 0) xor (idxY % 2 == 0)

            if (isPlayable) {
                when (idxX) {
                    in 0..2 -> boardArr[idxX][idxY] = Representation.BLACK
                    in 5..7 -> boardArr[idxX][idxY] = Representation.WHITE
                    else -> boardArr[idxX][idxY] = Representation.Playable
                }
            }
        }
    }
    constructor(boardArr: Array<Array<Representation>>){
        this.boardArr = boardArr
    }
}

fun movePiece(boardArr: Array<Array<Representation>>,fromPos: String, toPos: String,player: Player): Pair<Board?,PlayReturn> { //TODO() Talvez retornar um Pair com o board e a mensagem do tipo de erro caso exista
    val from = fromPos.toSquareOrNull()
    val to = toPos.toSquareOrNull()
    if (from != null && to != null) {
        val fromRow = ROW_DIM -1-from.row.index
        val fromCol = from.column.index
        val toRow = ROW_DIM -1-to.row.index
        val toCol = to.column.index
        if(isYourTurn(boardArr,fromRow,fromCol,player)) {
            val piece = boardArr[fromRow][fromCol]
            val mandatory = hasOtherMandatory(boardArr,player.symbol)
            if (mandatory.isNotEmpty()) {
                val aux = mandatory.firstOrNull {it.first.first == fromRow && it.first.second == fromCol }
                aux?.second?.forEach {
                    if (it.second == to) {
                        boardArr[fromRow][fromCol] = Representation.Playable
                        boardArr[ROW_DIM - it.first.row.index -1][it.first.column.index] = Representation.Playable
                        if(canBecomeKing(toRow,piece)) boardArr[toRow][toCol] = getRep(player.symbol.uppercaseChar())!!
                        else boardArr[toRow][toCol] = piece
                        return if(hasWon(boardArr,player.advance().symbol)) { //É só preciso verificar se foi ganho quando uma peça é comida
                            Pair(Board(boardArr),PlayReturn.YouWon)
                        } else {
                            if (hasOtherMandatory(boardArr,player.symbol).firstOrNull { next -> next.first.first == toRow && next.first.second == toCol } != null){
                                Pair(Board(boardArr),PlayReturn.PlayAgain)
                            }else {
                                Pair(Board(boardArr), PlayReturn.ValidPlay)
                            }
                        }
                    }
                }
                //Ver se é obrigatório comer
                return Pair(Board(boardArr),PlayReturn.MandatoryPlay)
            }
            if (isKing(boardArr[fromRow][fromCol])){
                if (canKingDoMove(fromRow, fromCol, toRow, toCol,boardArr)){
                    boardArr[toRow][toCol] = boardArr[fromRow][fromCol]
                    boardArr[fromRow][fromCol] = Representation.Playable
                    return Pair(Board(boardArr),PlayReturn.ValidPlay)
                }
            }else {
                //Se nao for obrigatório comer fazer um move normal
                if (canDoMove(fromRow, fromCol, toRow, toCol, boardArr)) {
                    boardArr[fromRow][fromCol] = Representation.Playable
                    if (canBecomeKing(toRow, piece)) boardArr[toRow][toCol] = getRep(player.symbol.uppercaseChar())!!
                    else boardArr[toRow][toCol] = getRep(player.symbol)!!
                    return Pair(Board(boardArr), PlayReturn.ValidPlay)
                }
            }
        }
    }
    return Pair(Board(boardArr),PlayReturn.InvalidPlay)
}

fun allTargets(boardArr: Array<Array<Representation>>,player: Player,fromPos: Square): List<Pair<Square, Square>> {
    val mandatory = hasOtherMandatory(boardArr,player.symbol)
    val list = premisesPieces(ROW_DIM - 1 - fromPos.row.index,fromPos.column.index,boardArr)
    if(mandatory.isNotEmpty()) return list
    return list.ifEmpty { piecesToMoveTo(boardArr,fromPos) }
}

fun piecesToMoveTo(boardArr: Array<Array<Representation>>,fromPos: Square): MutableList<Pair<Square, Square>> {
    val list: MutableList<Pair<Square,Square>> = mutableListOf()
    val fromRow = ROW_DIM -1-fromPos.row.index
    val fromCol = fromPos.column.index
    if(isKing(boardArr[fromRow][fromCol])) {
        for (toRowUpRight in fromRow+1 until ROW_DIM) {
            for (toColUpRight in fromCol+1 until COL_DIM) {
                if(canKingDoMove(fromRow, fromCol, toRowUpRight, toColUpRight, boardArr)) {
                    if (boardArr[toRowUpRight][toColUpRight] == Representation.Playable) {
                        list.add(Pair(fromPos, Square(ROW_DIM - 1 - toRowUpRight, toColUpRight)))
                    }
                }
            }
        }
        for (toRowUpLeft in fromRow+1 until ROW_DIM) {
            for (toColUpLeft in fromCol-1 downTo 0) {
                if(canKingDoMove(fromRow, fromCol, toRowUpLeft, toColUpLeft, boardArr)) {
                    if (boardArr[toRowUpLeft][toColUpLeft] == Representation.Playable) {
                        list.add(Pair(fromPos, Square(ROW_DIM - 1 - toRowUpLeft, toColUpLeft)))
                    }
                }
            }
        }
        for (toRowDownLeft in fromRow-1 downTo 0) {
            for (toColDownLeft in fromCol-1 downTo 0) {
                if(canKingDoMove(fromRow, fromCol, toRowDownLeft, toColDownLeft, boardArr)) {
                    if (boardArr[toRowDownLeft][toColDownLeft] == Representation.Playable) {
                        list.add(Pair(fromPos, Square(ROW_DIM - toRowDownLeft - 1, toColDownLeft)))
                    }
                }
            }
        }
        for (toRowDownRight in fromRow-1 downTo 0) {
            for (toColDownRight in fromCol+1 until COL_DIM) {
                if(canKingDoMove(fromRow, fromCol, toRowDownRight, toColDownRight, boardArr)) {
                    if (boardArr[toRowDownRight][toColDownRight] == Representation.Playable) {
                        list.add(Pair(fromPos, Square(ROW_DIM - toRowDownRight - 1, toColDownRight)))
                    }
                }
            }
        }
    }
    else {
        val toRowUpRight = fromRow+1
        val toColUpRight = fromCol+1
        if(canDoMove(fromRow,fromCol,toRowUpRight,toColUpRight,boardArr)) {
            if (boardArr[toRowUpRight][toColUpRight] == Representation.Playable)
                list.add(Pair(fromPos, Square(ROW_DIM - 1 - toRowUpRight, toColUpRight)))
        }

        val toRowUpLeft = fromRow+1
        val toColUpLeft = fromCol-1
        if(canDoMove(fromRow,fromCol,toRowUpLeft,toColUpLeft,boardArr)) {
            if (boardArr[toRowUpLeft][toColUpLeft] == Representation.Playable)
                list.add(Pair(fromPos, Square(ROW_DIM - 1 - toRowUpLeft, toColUpLeft)))
        }

        val toRowDownLeft = fromRow-1
        val toColDownLeft = fromCol-1
        if(canDoMove(fromRow,fromCol,toRowDownLeft,toColDownLeft,boardArr)) {
            if (boardArr[toRowDownLeft][toColDownLeft] == Representation.Playable)
                list.add(Pair(fromPos, Square(ROW_DIM - toRowDownLeft - 1, toColDownLeft)))
        }

        val toRowDownRight = fromRow-1
        val toColDownRight = fromCol+1
        if(canDoMove(fromRow,fromCol,toRowDownRight,toColDownRight,boardArr)) {
            if(boardArr[toRowDownRight][toColDownRight] == Representation.Playable)
                list.add(Pair(fromPos, Square(ROW_DIM - toRowDownRight - 1, toColDownRight)))
        }
    }
    return list
}
fun canKingDoMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, boardArr: Array<Array<Representation>>): Boolean {
    if (listOf(fromRow,fromCol,toRow,toCol).any { it >= BOARD_DIM || it < 0}) return false
    if (isDiagonal(fromRow-toRow,fromCol-toCol)){
        val diagonal = getDiagonal(toRow-fromRow,toCol-fromCol)
        for (i in 1..abs(toRow-fromRow)){
            if (boardArr[fromRow +i*diagonal.x ][fromCol+ i*diagonal.y]!= Representation.Playable ) {
                return false
            }
        }
        return true
    }
    return false
}

fun hasOtherMandatory(boardArr: Array<Array<Representation>>, user:Char): MutableList<Pair<Pair<Int, Int>, MutableList<Pair<Square, Square>>>> {
    val filteredPos = mutableListOf<Pair<Pair<Int,Int>, MutableList<Pair<Square, Square>>>>()
    for (row in boardArr.indices){
        for (col in boardArr.indices){
            if (boardArr[row][col] == getRep(user) || boardArr[row][col] == getRep(user.uppercaseChar())) {
                val auxList = isMandatoryEat(row, col, boardArr)
                if (auxList.isNotEmpty()) {
                    filteredPos.add(Pair(Pair(row, col), auxList))
                }
            }
        }
    }
    return filteredPos

}

fun canDoMove(fromRow: Int, fromCol:Int, toRow: Int, toCol: Int, boardArr: Array<Array<Representation>>):Boolean {
    if (listOf(fromRow,fromCol,toRow,toCol).any { it >= BOARD_DIM || it < 0}) return false
    val oneSquareD = abs(fromRow - toRow) ==1 && abs(fromCol - toCol) ==1
    if(oneSquareD) {
        if(boardArr[fromRow][fromCol] == Representation.BLACK && fromRow < toRow ||
           boardArr[fromRow][fromCol] == Representation.WHITE && fromRow > toRow) {
            if (boardArr[fromRow][fromCol] != Representation.Playable) {
                return true
            }
        }
    }
    return false
}

fun isYourTurn(boardArr: Array<Array<Representation>>,fromRow: Int,fromCol: Int,player: Player): Boolean {
    if(getOwn(getRep(player.symbol)!!).contains(boardArr[fromRow][fromCol]) ) return true
    return false
}

fun isMandatoryEat(fromRow: Int,fromCol: Int, boardArr: Array<Array<Representation>>): MutableList<Pair<Square, Square>> {
    return premisesPieces(fromRow,fromCol,boardArr)

}

fun getPremise(boardArr: Array<Array<Representation>>,fromRow: Int, fromCol: Int,orientation:Diagonal,list: MutableList<Pair<Square, Square>>){
    if (hasOpponent(boardArr, fromRow, fromCol, orientation,1)) {
        if (hasPlayable(boardArr, fromRow, fromCol, orientation,2)) {
            list.add(
                    Pair(
                        Square(ROW_DIM - (fromRow + orientation.x) - 1, fromCol + orientation.y),
                        Square(ROW_DIM - (fromRow + orientation.x * 2) - 1, fromCol + orientation.y * 2)
                    )
            )
        }
    }
}
fun getPremiseKing(boardArr: Array<Array<Representation>>,fromRow: Int, fromCol: Int, minOf: Int,orientation:Diagonal,list: MutableList<Pair<Square, Square>>){
    for (i in 1 .. minOf){
        //search until find a diagonal of blank space, an opponent piece, and a single blank space,
        // if two opponent pieces are found in a row, a play in that diagonal is not possible
        if (hasOpponent(boardArr, fromRow, fromCol, orientation, i)) {
            if (hasPlayable(boardArr, fromRow, fromCol, orientation, i+1)) {
                list.add(
                        Pair(
                            Square(ROW_DIM - (fromRow + orientation.x * i)-1, fromCol + orientation.y * i),
                            Square(ROW_DIM - (fromRow + orientation.x * (i + 1))-1, fromCol + orientation.y * (i + 1))
                        )
                )
            }else{
                break
            }
        }
    }

}

fun hasOpponent(boardArr: Array<Array<Representation>>,fromRow: Int, fromCol: Int,orientation:Diagonal, i: Int) =
        isLimit(fromRow,fromCol,orientation,i) && getOpponent(boardArr[fromRow][fromCol]).contains(boardArr[fromRow + orientation.x * i][fromCol + orientation.y * i])

fun hasPlayable(boardArr: Array<Array<Representation>>,fromRow: Int, fromCol: Int,orientation:Diagonal, i: Int) =
        isLimit(fromRow,fromCol,orientation, i) && boardArr[fromRow + orientation.x * i][fromCol + orientation.y * i ] == Representation.Playable


fun isLimit(fromRow: Int,fromCol: Int,orientation: Diagonal,i:Int) = fromRow + orientation.x * i in 0 until ROW_DIM && fromCol + orientation.y * i in 0 until ROW_DIM

fun premisesPieces(fromRow: Int, fromCol: Int, boardArr: Array<Array<Representation>>): MutableList<Pair<Square,Square>> {
    val list: MutableList<Pair<Square,Square>> = mutableListOf() //TODO(): Mudar para nao ser mutavel
    val king = isKing(boardArr[fromRow][fromCol])
    if (king){
        getPremiseKing(boardArr,fromRow,fromCol,minOf(fromRow,ROW_DIM - fromCol -1),Diagonal.UP_RIGHT,list)
        getPremiseKing(boardArr,fromRow,fromCol,minOf(fromRow,fromCol),Diagonal.UP_LEFT,list)
        getPremiseKing(boardArr,fromRow,fromCol,minOf(ROW_DIM - fromRow -1),Diagonal.DOWN_RIGHT,list)
        getPremiseKing(boardArr,fromRow,fromCol,minOf(ROW_DIM - fromRow -1),Diagonal.DOWN_LEFT,list)

    }else{
        getPremise(boardArr,fromRow,fromCol,Diagonal.UP_RIGHT,list)
        getPremise(boardArr,fromRow,fromCol,Diagonal.UP_LEFT,list)
        getPremise(boardArr,fromRow,fromCol,Diagonal.DOWN_RIGHT,list)
        getPremise(boardArr,fromRow,fromCol,Diagonal.DOWN_LEFT,list)
    }


    return list
}

fun refreshBoard(board: Array<Array<Representation>>) = Board(board)

fun canBecomeKing(toRow: Int, rep: Representation): Boolean {
    if(rep.symbol == 'w' && toRow == 0) return true
    if(rep.symbol == 'b' && toRow == 7) return true
    return false
}

fun toStringBoardForTests(boardArr: Array<Array<Representation>>):String {
    var board1 = "   +---------------+"
    Square.values.forEach { pos ->
        if (pos.column.index == 0)
            board1 += "\n${if (pos.row.index == 8) "" else " "}${pos.row.index + 1} |"
        if (pos.column.index == 7)
            board1 += boardArr[(ROW_DIM - 1 - pos.row.index)][pos.column.index].symbol
        else
            board1 += "${boardArr[(ROW_DIM - 1 - pos.row.index)][pos.column.index].symbol} "
        if (pos.column.index == 7) {
            board1 += "|"
        }
    }
    board1 += "\n   +---------------+\n    A B C D E F G H"
    return board1
}

fun hasWon(boardArr: Array<Array<Representation>>, user:Char): Boolean {
    for (row in boardArr.indices){
        for (col in boardArr.indices){
            if (boardArr[row][col] == getRep(user) || boardArr[row][col] == getRep(user.uppercaseChar()))
                return false
        }
    }
    return true
}
