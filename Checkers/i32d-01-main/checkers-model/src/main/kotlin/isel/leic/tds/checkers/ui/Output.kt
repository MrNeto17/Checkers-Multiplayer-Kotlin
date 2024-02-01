package isel.leic.tds.checkers.ui


import isel.leic.tds.checkers.board.Checkers
import isel.leic.tds.checkers.board.ROW_DIM
import isel.leic.tds.checkers.board.Square

/**
 * Represents the board before we use the start command
 * @param board
 * @return Unit
 */
fun toStringBoard(game: Checkers, currentPlayer: String):String {
    var board1 = "   +---------------+  Turn = $currentPlayer Player = ${game.player?.symbol}"
    if (game.board != null) {
        Square.values.forEach { pos ->
            if (pos.column.index == 0)
                board1 += "\n${if (pos.row.index == 8) "" else " "}${pos.row.index + 1} |"
            if (pos.column.index == 7)
                board1 += game.board.boardArr[(ROW_DIM - 1 - pos.row.index)][pos.column.index].symbol
            else
                board1 += "${game.board.boardArr[(ROW_DIM - 1 - pos.row.index)][pos.column.index].symbol} "
            if (pos.column.index == 7) {
                board1 += "|"
            }
        }
        board1 += "\n   +---------------+\n    A B C D E F G H\n"
    }

    return board1
}

/*
   +-----------------+ Turn = b
 1 |                 | Player = w
 2 |                 |
 3 |                 |
 4 |                 |
 5 |                 |
 6 |                 |
 7 |                 |
 8 |                 |
   +-----------------+
     A B C D E F G H
 */
