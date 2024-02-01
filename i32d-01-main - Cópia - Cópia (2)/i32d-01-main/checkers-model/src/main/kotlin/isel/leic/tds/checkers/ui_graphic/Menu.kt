package isel.leic.tds.checkers.ui_graphic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import kotlin.system.exitProcess

@Composable
fun FrameWindowScope.CheckersMenu(onNew: (String) -> Unit,refresh: () -> Unit,targets: () -> Unit,state: GameState) {
    val (newGameDialog, setNewGameDialog) = remember { mutableStateOf(false) }

    if(newGameDialog)
        DialogInput({ name ->
            onNew(name)
            setNewGameDialog(false)
        }) {
            setNewGameDialog(false)
        }
    MenuBar {
        Menu("Game") {
            Item("Start",onClick = { setNewGameDialog(true) })
            Item("Refresh", enabled = state.game.gameName != null, onClick = { refresh.invoke() })
            Item("Exit", onClick = { exitProcess(0) })
        }
        Menu("Options") {
            CheckboxItem("Show Targets", onCheckedChange = {
                state.checkedTargets = !state.checkedTargets
            }, checked = state.checkedTargets)
            CheckboxItem("Auto-Refresh", onCheckedChange = {
                state.checkedAutoRefresh = !state.checkedAutoRefresh
                if (state.checkedAutoRefresh)
                    state.autoRefresh()
                else
                    state.jobRefresh.cancel()
            }, checked = state.checkedAutoRefresh)
        }
    }
}