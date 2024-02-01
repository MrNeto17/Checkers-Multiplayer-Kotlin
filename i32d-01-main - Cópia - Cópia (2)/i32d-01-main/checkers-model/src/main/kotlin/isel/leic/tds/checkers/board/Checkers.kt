package isel.leic.tds.checkers.board

import isel.leic.tds.checkers.storage.StorageAsync
import kotlin.math.abs

data class Checkers(val board: Board?, val player: Player?, val gameName: String?)

enum class Player(val symbol: Char) {
    WHITE('w'), BLACK('b');
    fun advance() = if (this === WHITE) BLACK else WHITE
}

enum class Diagonal(val x:Int,val y:Int){
    UP_RIGHT(-1,1),
    UP_LEFT(-1,-1),
    DOWN_RIGHT(1,1),
    DOWN_LEFT(1,-1)
}

fun getDiagonal(x: Int,y: Int):Diagonal{
    if (x/abs(x) == 1 && y/abs(y) == 1)
        return Diagonal.DOWN_RIGHT
    if (x/abs(x) == -1 && y/abs(y) == -1)
        return Diagonal.UP_LEFT

    if (x/abs(x) == -1 && y/abs(y) == 1)
        return Diagonal.UP_RIGHT
    if (x/abs(x) == 1 && y/abs(y) == -1)
        return Diagonal.DOWN_LEFT
    else
        throw IllegalStateException("this is not a diagonal")
}

fun isDiagonal(x: Int,y: Int) = abs(x) == abs(y) && abs(x) != 0

enum class Representation(val symbol: Char){
    WHITE(Player.WHITE.symbol), BLACK(Player.BLACK.symbol), Playable('-'), NonPlayable(' '),KING_W('W'), KING_B('B')
}
fun isKing(rep:Representation) = rep == Representation.KING_B || rep == Representation.KING_W

fun getOpponent(rep: Representation): List<Representation> {
    return when(rep){
        Representation.KING_W -> listOf(Representation.KING_B,Representation.BLACK)
        Representation.WHITE -> listOf(Representation.KING_B,Representation.BLACK)
        Representation.KING_B -> listOf(Representation.KING_W,Representation.WHITE)
        Representation.BLACK -> listOf(Representation.KING_W,Representation.WHITE)
        else -> {
            listOf()
        }
    }
}

fun getOwn(rep: Representation): List<Representation> {
    return when(rep){
        Representation.BLACK-> listOf(Representation.KING_B,Representation.BLACK)
        Representation.WHITE -> listOf(Representation.KING_W,Representation.WHITE)
        else -> {
            listOf()
        }
    }
}

fun getRep(rep:Char): Representation? {
    return when(rep){
        Representation.WHITE.symbol -> Representation.WHITE
        Representation.BLACK.symbol -> Representation.BLACK
        Representation.Playable.symbol -> Representation.Playable
        Representation.NonPlayable.symbol -> Representation.NonPlayable
        Representation.KING_W.symbol -> Representation.KING_W
        Representation.KING_B.symbol -> Representation.KING_B
        else -> null
    }
}

enum class PlayMessage { NONE, INVALID_TURN,INVALID_PLAY,MANDATORY_PLAY, NOT_YOUR_TURN, YOU_WON, YOU_LOST}

data class PlayResult(val checkers: Checkers, val error: PlayMessage)

suspend fun Checkers.startGame(gameName: String, st: StorageAsync): Checkers {
    checkNotNull(this) { "Game not started yet" }
    val board = Board()
    if (!st.existsFile(gameName)) {
        st.start(gameName, board)
        return this.copy(board = board, player = Player.WHITE,gameName = gameName)
    }
    return this.copy(board = board, player = Player.BLACK,gameName = gameName)
}

suspend fun Checkers.play(st: StorageAsync, fromPos: Square, toPos: Square): PlayResult {
    checkNotNull(this) { "Game not started yet" }
    require(this.gameName != null && this.player != null)
    val currPlayer = st.getTurn(this.gameName)
    check(currPlayer == this.player.name) {
        return PlayResult(this,PlayMessage.NOT_YOUR_TURN)
    }
    val board = st.getBoard(this.gameName)

    val resBoard = movePiece(board,fromPos.toString(),toPos.toString(), this.player)
    return when (resBoard.second) {
        is PlayReturn.InvalidPlay -> PlayResult(this,PlayMessage.INVALID_PLAY)
        is PlayReturn.MandatoryPlay -> PlayResult(this,PlayMessage.MANDATORY_PLAY)
        is PlayReturn.YouWon -> {
            val final = this.copy(board = resBoard.first,player = this.player, gameName = this.gameName)
            st.save(final)
            return PlayResult(final,PlayMessage.YOU_WON)
        }
        else -> {
            val gameAux = this.copy(board = resBoard.first, player = this.player, gameName = this.gameName)
            if (gameAux.gameName != null && gameAux.player != null && gameAux.board != null) {
                if(resBoard.second == PlayReturn.PlayAgain){
                    st.play(gameAux.gameName, gameAux.player, gameAux.board)
                }else{
                    st.play(gameAux.gameName, gameAux.player.advance(), gameAux.board)
                }
            }
            PlayResult(gameAux,PlayMessage.NONE)
        }
    }
}

suspend fun refreshGame(game: Checkers?, st: StorageAsync): PlayResult {
    checkNotNull(game) { "Game has not started" }
    checkNotNull(game.gameName)
    checkNotNull(game.player)
    val result = refreshBoard(st.getBoard(game.gameName))
    val playMessage = if(hasWon(result.boardArr,game.player.advance().symbol)){
        println("You Won")
        PlayMessage.YOU_WON
    }
    else if(hasWon(result.boardArr,game.player.symbol)) {
        println("You Lost")
        PlayMessage.YOU_LOST
    }
    else PlayMessage.NONE
    return PlayResult(game.copy(board = result , player = game.player,gameName = game.gameName),playMessage)
}

suspend fun Checkers.allTargets(fromPos: Square):List<Pair<Square, Square>> {
    checkNotNull(this) { "Game has not started" }
    checkNotNull(this.gameName)
    checkNotNull(this.player)
    return allTargets(this.board!!.boardArr,this.player,fromPos)
}

