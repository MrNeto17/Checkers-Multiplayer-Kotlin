package isel.leic.tds.checkers.ui_graphic

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import isel.leic.tds.checkers.storage.StorageAsync
import kotlinx.coroutines.launch

@Composable
fun outsideView(state: GameState) {
    Text("Game Name: ${if(state.game.gameName == null) "" else state.game.gameName}")
    Text("Turn: ${state.turn}")
    Text("Player: ${if(state.game.gameName == null) "" else state.game.player}")
}