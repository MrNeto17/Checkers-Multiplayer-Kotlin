package isel.leic.tds.checkers.ui_graphic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import isel.leic.tds.checkers.board.*
import isel.leic.tds.checkers.storage.StorageAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameState(val storage: StorageAsync, val scope: CoroutineScope) {
    var jobRefresh: Job = Job()
    var game by mutableStateOf(Checkers(Board(), Player.WHITE,null))
        private set
    var openDialogName by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set
    var fromPos by mutableStateOf<Square?>(null)
    var toPos by mutableStateOf<Square?>(null)
    var allTargets by mutableStateOf<List<Pair<Square, Square>>>(emptyList())
    var checkedAutoRefresh by mutableStateOf(false)
    var checkedTargets by mutableStateOf(false)
    var turn by mutableStateOf("")

    fun resetBoxes(){
        checkedAutoRefresh = false
        checkedTargets = false
    }

    fun start(name: String) = scope.launch{
        game = game.startGame(name, storage)
        turn = storage.getTurn(game.gameName!!)
    }

    fun play(pos: Square) {
        scope.launch {
            if(fromPos == pos) fromPos = null
            if(fromPos == null && toPos == null){
                fromPos = pos
                if(checkedTargets) {
                    allTargets = game.allTargets(fromPos!!)
                }
            }
            else if(fromPos != null) {
                if(game.board!!.boardArr[ROW_DIM-1 - fromPos!!.row.index][fromPos!!.column.index] == game.board!!.boardArr[ROW_DIM -1 -pos.row.index][pos.column.index]) {
                    allTargets = emptyList()
                    fromPos = pos
                    if(checkedTargets) {
                        allTargets = game.allTargets(fromPos!!)
                    }
                }
                else if (toPos == null)
                    toPos = pos
            }
            if(fromPos != null && toPos != null) {
                val (g,error) = game.play(storage, fromPos!!, toPos!!)
                when (error) {
                    PlayMessage.NONE -> {
                        game = g
                    }
                    PlayMessage.YOU_WON -> {
                        game = g
                        message = "You Won"
                        jobRefresh.cancel()
                        game = Checkers(Board(), Player.WHITE,null)
                        resetBoxes()
                    }
                    else -> {
                        message = when(error) {
                            PlayMessage.INVALID_PLAY -> "Invalid Play"
                            PlayMessage.MANDATORY_PLAY -> "You have mandatory plays"
                            PlayMessage.NOT_YOUR_TURN -> "Not your turn"
                            else -> null
                        }
                    }
                }
                allTargets = emptyList()
                fromPos = null
                toPos = null
            }
        }
    }
    fun refresh() {
        scope.launch {
            if (game.gameName != null) {
                val (g,error) =  refreshGame(game,storage)
                turn = storage.getTurn(game.gameName!!)
                when (error) {
                    PlayMessage.YOU_WON -> {
                        game = g
                        message = "You Won"
                        game = Checkers(Board(), Player.WHITE,null)
                        resetBoxes()
                    }
                    PlayMessage.YOU_LOST -> {
                        game = g
                        message = "You Lost"
                        jobRefresh.cancel()
                        storage.removeDoc(game.gameName!!) //TODO(): Em vez de apagar logo se for criado um novo jogo com o mesmo nome dar replace
                        game = Checkers(Board(), Player.WHITE,null)
                        resetBoxes()
                    }
                    else -> {
                        game = g
                    }
                }
            }
        }
    }

    fun autoRefresh() {
        if (checkedAutoRefresh) {
            jobRefresh = scope.launch {
                while (true) {
                    delay(2000L)
                    refresh()
                }
            }
        }
    }

    fun showTargets() {
        scope.launch {
            if(fromPos != null) {
                allTargets = game.allTargets(fromPos!!)
            }
        }
    }

    fun closeDialog() {
        openDialogName = false
    }

    fun messageAck() {
        message = null
    }
}

/* TODO():
 * Resolver bug quando o jogador perde para criar um novo jogo da erro
 * Mostrar os targets para o rei
 */