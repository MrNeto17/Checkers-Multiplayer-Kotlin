package isel.leic.tds.checkers.storage

import isel.leic.tds.checkers.board.Board
import isel.leic.tds.checkers.board.Checkers
import isel.leic.tds.checkers.board.Player
import isel.leic.tds.checkers.board.Representation

/**
 * Basic operations for persistence of game information.
 */
interface StorageAsync {

    suspend fun closeConnection()

    suspend fun start(name: String,board: Board)

    suspend fun save(game: Checkers)

    suspend fun existsFile(name: String):Boolean

    suspend fun removeDoc(gameName: String)

    suspend fun play(gameName: String, player: Player, board: Board)

    suspend fun getTurn(name: String): String

    suspend fun getBoard(name: String): Array<Array<Representation>>
}