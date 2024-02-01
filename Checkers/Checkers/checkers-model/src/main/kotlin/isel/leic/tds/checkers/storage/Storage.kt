package isel.leic.tds.checkers.storage
import isel.leic.tds.checkers.board.Board
import isel.leic.tds.checkers.board.Checkers
import isel.leic.tds.checkers.board.Player
import isel.leic.tds.checkers.board.Representation

/**
 * Basic operations for persistence of game information.
 */
interface Storage {

    fun start(name: String,board: Board)

    fun save(game: Checkers)

    fun existsFile(name: String): Boolean

    fun removeDoc(gameName: String)

    fun play(gameName: String, player: Player, board: Board)

    fun getTurn(name: String): String

    fun getBoard(name: String): Array<Array<Representation>>
}