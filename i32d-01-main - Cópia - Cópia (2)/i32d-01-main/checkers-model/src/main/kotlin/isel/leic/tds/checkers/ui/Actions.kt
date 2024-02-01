package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.board.*
import isel.leic.tds.checkers.storage.Storage
import java.lang.IllegalStateException

fun startAction(game: Checkers?, args: List<String>, st: Storage): Checkers {
    checkNotNull(game) { "Game not started yet" }
    require(args.isNotEmpty()) { "Missing game name" }
    val board = Board()
    val gameName = args[0]
    if (st.existsFile(gameName) == null) {
        st.start(args[0], board)
        return game.copy(board = board, player = Player.WHITE,gameName = gameName)
    }
    return game.copy(board = board, player = Player.BLACK,gameName = gameName)
}

fun playAction(game: Checkers?, args: List<String>, st: Storage): Checkers {
    checkNotNull(game) { "Game not started yet" }
    require(args.isNotEmpty()) { "Missing positions" }
    require(game.gameName != null && game.player != null)
    if(hasWon(game.board!!.boardArr,game.player.advance().symbol)){
        println("You Won")
        return game
    }
    if(hasWon(game.board.boardArr,game.player.symbol)){
        println("You Lost")
        return game
    }
    val currPlayer = st.getTurn(game.gameName)
    check(currPlayer == game.player.name) { "Not your turn"}
    val fromPos = args[0]
    val toPos = args[1]
    val board = st.getBoard(game.gameName)

    val resBoard = movePiece(board,fromPos,toPos, game.player)
    return when (resBoard.second) {
            is PlayReturn.InvalidPlay -> throw IllegalStateException("Invalid Play")
            is PlayReturn.MandatoryPlay -> throw IllegalStateException("You have mandatory plays")
            is PlayReturn.YouWon -> {
                val final = game.copy(board = resBoard.first,player = game.player, gameName = game.gameName)
                st.save(final)
                if(hasWon(final.board!!.boardArr,game.player.advance().symbol)) {
                    println("You Won")
                }
                return final
            }
            else -> {
                val gameAux = game.copy(board = resBoard.first, player = game.player, gameName = game.gameName)
                if (gameAux.gameName != null && gameAux.player != null && gameAux.board != null) {
                    if(resBoard.second == PlayReturn.PlayAgain){
                        st.play(gameAux.gameName, gameAux.player, gameAux.board)
                    }else{
                        st.play(gameAux.gameName, gameAux.player.advance(), gameAux.board)
                    }
                }
                gameAux
            }
        }
}

fun refreshAction(game: Checkers?, st: Storage): Checkers {
    checkNotNull(game) { "Game has not started" }
    checkNotNull(game.gameName)
    checkNotNull(game.player)
    val result = refreshBoard(st.getBoard(game.gameName))
    if(hasWon(result.boardArr,game.player.advance().symbol)){
        println("You Won")
    }
    if(hasWon(result.boardArr,game.player.symbol)){
        println("You Lost")
    }
    return game.copy(board = result , player = game.player,gameName = game.gameName)
}
