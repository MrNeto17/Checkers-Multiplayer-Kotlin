package isel.leic.tds.checkers.storage

import isel.leic.tds.checkers.board.Board
import isel.leic.tds.checkers.board.Checkers
import isel.leic.tds.checkers.board.Player
import isel.leic.tds.checkers.board.Representation
import isel.leic.tds.checkers.client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.litote.kmongo.coroutine.*

private const val collectionName = "games"

class MongoStorageAsync(private val driver:CoroutineDatabase) : StorageAsync {
    /**
     * Representation of the game state in a document.
     */
   data class Doc(
        val _id: String,
        val currPlayer:String,
        val boardArr: Array<Array<Representation>>
    )

    /**
     * The collection with all the games.
     */
    private val col = driver.getCollection<Doc>(collectionName)

    /**
     * Saves the data in the document when we start a game
     * @param name
     * @param board
     */
    override suspend fun start(name: String, board: Board) {
        val doc = col.findOneById(name)
        if (doc == null){
            col.insertOne(Doc(name, Player.WHITE.name,board.boardArr))
        }
        col.replaceOneById(name,Doc(name, Player.WHITE.name,board.boardArr))
    }

    /**
     * Checks if the file exists
     * @param name
     * @return Boolean
     */
    override suspend fun existsFile(name: String): Boolean {
        col.findOneById(name) ?: return false
        return true
    }

    /**
     * Saves to the file the affected board
     * @param boolean
     * @param board
     * @param gameName
     * @param check
     * @return Unit
     */
    override suspend fun play(gameName: String, player: Player, board: Board) {
        val doc = col.findOneById(gameName)
        if (doc != null) {
            col.replaceOneById(gameName,Doc(doc._id,player.name,board.boardArr))

        }
    }

    override suspend fun save(game: Checkers) {
        col.replaceOneById(game.gameName!!,
            Doc(
                game.gameName,
                game.player!!.name,
                game.board!!.boardArr
            )
        )
    }

    /**
     * Removes the document when we use the command exit or win a game
     * @param gameName
     * @return Unit
     */
    override suspend fun removeDoc(gameName: String) {
        val game = col.findOneById(gameName)
        if (game != null) col.deleteOneById(game._id)
        //closeConnection()
    }

    override suspend fun closeConnection(){
        withContext(Dispatchers.IO){
            client.close()
        }
    }

    override suspend fun getTurn(name: String): String {
        val doc = col.findOneById(name)
        if(doc != null) return doc.currPlayer
        return ""
    }

    override suspend fun getBoard(name: String): Array<Array<Representation>> {
        val doc = col.findOneById(name)
        return doc!!.boardArr
    }
}