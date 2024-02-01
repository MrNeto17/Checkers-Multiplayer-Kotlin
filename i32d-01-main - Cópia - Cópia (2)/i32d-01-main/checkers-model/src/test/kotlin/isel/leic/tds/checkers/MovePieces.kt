package isel.leic.tds.checkers

import isel.leic.tds.checkers.board.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MovePieces {
    @Test
    fun `Moving white piece simple`() {
        val board = Board()
        //Move white Piece
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  b   b   b   b|\n" +
                    " 7 |b   b   b   b  |\n" +
                    " 6 |  b   b   b   b|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  w   -   -   -|\n" +
                    " 3 |-   w   w   w  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H",
            toStringBoardForTests(movePiece(board.boardArr, "3a", "4b", Player.WHITE).first!!.boardArr)
        )
    }

    @Test
    fun `Moving black piece simple`() {
        val board = Board()
        //Move black Piece
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  b   b   b   b|\n" +
                    " 7 |b   b   b   b  |\n" +
                    " 6 |  -   b   b   b|\n" +
                    " 5 |-   b   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |w   w   w   w  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H",
            toStringBoardForTests(movePiece(board.boardArr, "6b", "5c", Player.BLACK).first!!.boardArr)
        )
    }

    @Test
    fun `Eat black piece and white piece forward`() {
        val board = Board()
        //Move white Piece
        movePiece(board.boardArr, "3a", "4b", Player.WHITE)
        //Move black Piece
        movePiece(board.boardArr, "6d", "5c", Player.BLACK)

        assertEquals(
            "   +---------------+\n" +
                    " 8 |  b   b   b   b|\n" +
                    " 7 |b   b   b   b  |\n" +
                    " 6 |  b   w   b   b|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   w   w   w  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H",
            toStringBoardForTests(movePiece(board.boardArr, "4b", "6d", Player.WHITE).first!!.boardArr)
        )

        assertEquals(
            "   +---------------+\n" +
                    " 8 |  b   b   b   b|\n" +
                    " 7 |b   -   b   b  |\n" +
                    " 6 |  b   -   b   b|\n" +
                    " 5 |-   -   b   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   w   w   w  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H",
            toStringBoardForTests(movePiece(board.boardArr, "7c", "5e", Player.BLACK).first!!.boardArr)
        )

    }

    @Test
    fun `Eat black piece backwards`() {
        val board = Board()
        println(toStringBoardForTests(board.boardArr))
        //Move white Piece
        val res = movePiece(board.boardArr, "3a", "4b", Player.WHITE)
        println(toStringBoardForTests(res.first!!.boardArr))
        //Move black Piece
        val res1 = movePiece(res.first!!.boardArr, "6d", "5c", Player.BLACK)
        println(toStringBoardForTests(res1.first!!.boardArr))
        //Move white Piece
        val res2 = movePiece(res1.first!!.boardArr, "4b", "6d", Player.WHITE)
        println(toStringBoardForTests(res2.first!!.boardArr))
        //Move black Piece
        val res3 = movePiece(res2.first!!.boardArr, "7c", "5e", Player.BLACK)
        println(toStringBoardForTests(res3.first!!.boardArr))
        res3.first!!.boardArr[2][3] = Representation.WHITE
        println(toStringBoardForTests(res3.first!!.boardArr))
        //Move white Piece
        val res4 = movePiece(res3.first!!.boardArr, "6d", "4f", Player.WHITE)

        assertEquals(
            "   +---------------+\n" +
                    " 8 |  b   b   b   b|\n" +
                    " 7 |b   -   b   b  |\n" +
                    " 6 |  b   -   b   b|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   w   -|\n" +
                    " 3 |-   w   w   w  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(res4.first!!.boardArr)
        )
    }
    @Test
    fun `Eat two or more pieces in a row`(){
        val board = Board()
        //Move white Piece
        movePiece(board.boardArr, "3e", "4d", Player.WHITE)
        //Move black Piece
        movePiece(board.boardArr, "6b", "5a", Player.BLACK)
        //Move white Piece
        movePiece(board.boardArr, "2f", "3e", Player.WHITE)
        //Move black Piece
        movePiece(board.boardArr, "7c", "6b", Player.BLACK)
        //Move white Piece
        movePiece(board.boardArr, "4d", "5c", Player.WHITE)
        //Move black Piece
        var res = movePiece(board.boardArr, "6b", "4d", Player.BLACK)
        println(toStringBoardForTests(res.first!!.boardArr))
        assertEquals(PlayReturn.PlayAgain,res.second)

        res = movePiece(board.boardArr, "7a", "6b", Player.BLACK)
        assertEquals(res.second,PlayReturn.MandatoryPlay)

        res = movePiece(board.boardArr, "4d", "2f", Player.BLACK)
        println(toStringBoardForTests(res.first!!.boardArr))
        assertEquals(res.second,PlayReturn.PlayAgain)

        res = movePiece(board.boardArr, "2f", "4h", Player.BLACK)
        println(toStringBoardForTests(res.first!!.boardArr))
        assertEquals(res.second,PlayReturn.ValidPlay)
    }

    @Test
    fun `Can become King and Win Game`() {
        val board = Board()
        //Move white Piece
        movePiece(board.boardArr, "3a", "4b", Player.WHITE)
        println(toStringBoardForTests(board.boardArr))
        //Move black Piece
        movePiece(board.boardArr, "6d", "5c", Player.BLACK)
        println(toStringBoardForTests(board.boardArr))
        //Move white Piece
        movePiece(board.boardArr, "4b", "6d", Player.WHITE)
        println(toStringBoardForTests(board.boardArr))
        //Move black Piece
        movePiece(board.boardArr, "7c", "5e", Player.BLACK)
        println(toStringBoardForTests(board.boardArr))
        //Move white Piece
        movePiece(board.boardArr, "3g", "4f", Player.WHITE)
        println(toStringBoardForTests(board.boardArr))
        movePiece(board.boardArr, "4f", "6d", Player.WHITE)
        println(toStringBoardForTests(board.boardArr))
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  b   b   b   b|\n" +
                    " 7 |b   -   b   b  |\n" +
                    " 6 |  b   w   b   b|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   w   w   -  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(board.boardArr)
        )

        //Move black Piece
        board.boardArr[1][4] = Representation.Playable
        movePiece(board.boardArr, "8b", "7c", Player.BLACK)
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  -   b   b   b|\n" +
                    " 7 |b   b   -   b  |\n" +
                    " 6 |  b   w   b   b|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   w   w   -  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(board.boardArr)
        )
        //Move white Piece
        movePiece(board.boardArr, "6d", "8b", Player.WHITE)
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  W   b   b   b|\n" +
                    " 7 |b   -   -   b  |\n" +
                    " 6 |  b   -   b   b|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   w   w   -  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(board.boardArr)
        )
        board.boardArr[0][3] = Representation.Playable
        board.boardArr[0][5] = Representation.Playable
        board.boardArr[0][7] = Representation.Playable
        board.boardArr[1][0] = Representation.Playable
        board.boardArr[1][6] = Representation.Playable
        board.boardArr[2][1] = Representation.Playable
        board.boardArr[2][5] = Representation.Playable
        board.boardArr[2][7] = Representation.Playable

        assertEquals(
            "   +---------------+\n" +
                    " 8 |  W   -   -   -|\n" +
                    " 7 |-   -   -   -  |\n" +
                    " 6 |  -   -   -   -|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   w   w   -  |\n" +
                    " 2 |  w   w   w   w|\n" +
                    " 1 |w   w   w   w  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(board.boardArr)
        )
        assertTrue(hasWon(board.boardArr,'b'))
        assertFalse(hasWon(board.boardArr,'w'))
    }

    @Test
    fun `move king more than one space`(){
        val emptyBoard = EmptyBoard().board
        emptyBoard[0][1] = Representation.KING_W
        emptyBoard[6][1] = Representation.BLACK
        println( toStringBoardForTests(emptyBoard))
        movePiece(emptyBoard,"8b","2h",Player.WHITE)
        println( toStringBoardForTests(emptyBoard))
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  -   -   -   -|\n" +
                    " 7 |-   -   -   -  |\n" +
                    " 6 |  -   -   -   -|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   -   -   -  |\n" +
                    " 2 |  b   -   -   W|\n" +
                    " 1 |-   -   -   -  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(emptyBoard)
        )
    }

    @Test
    fun `move king and eat, win the game`(){
        val emptyBoard = EmptyBoard().board
        emptyBoard[0][1] = Representation.KING_W
        emptyBoard[2][3] = Representation.BLACK
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  W   -   -   -|\n" +
                    " 7 |-   -   -   -  |\n" +
                    " 6 |  -   b   -   -|\n" +
                    " 5 |-   -   -   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   -   -   -  |\n" +
                    " 2 |  -   -   -   -|\n" +
                    " 1 |-   -   -   -  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(emptyBoard)
        )
        val res = movePiece(emptyBoard,"8b","5e",Player.WHITE)
        println( toStringBoardForTests(res.first!!.boardArr))
        assertEquals(
            "   +---------------+\n" +
                    " 8 |  -   -   -   -|\n" +
                    " 7 |-   -   -   -  |\n" +
                    " 6 |  -   -   -   -|\n" +
                    " 5 |-   -   W   -  |\n" +
                    " 4 |  -   -   -   -|\n" +
                    " 3 |-   -   -   -  |\n" +
                    " 2 |  -   -   -   -|\n" +
                    " 1 |-   -   -   -  |\n" +
                    "   +---------------+\n" +
                    "    A B C D E F G H", toStringBoardForTests(emptyBoard)
        )
        assertEquals(PlayReturn.YouWon,res.second)
    }
}

class EmptyBoard{
    val board = Array(8){ Array(8){Representation.NonPlayable} }
    init {
        Square.values.forEach {
            val idxX = ROW_DIM - 1 - it.row.index
            val idxY = it.column.index
            val isPlayable = (idxX % 2 == 0) xor (idxY % 2 == 0)

            if (isPlayable) {
                board[idxX][idxY] = Representation.Playable
            }
        }
    }
}

