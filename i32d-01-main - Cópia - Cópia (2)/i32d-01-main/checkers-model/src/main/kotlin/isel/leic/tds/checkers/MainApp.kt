package isel.leic.tds.checkers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.mongodb.ConnectionString
import isel.leic.tds.checkers.storage.MongoStorageAsync
import isel.leic.tds.checkers.ui_graphic.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val connectionString =
    ConnectionString("mongodb+srv://1234:1234@cluster0.ttobwvd.mongodb.net/?retryWrites=true&w=majority")
val client = KMongo.createClient(connectionString).coroutine
val database = client.getDatabase("TDS")

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = WindowState(
            position = WindowPosition(Alignment.Center),
            size = DpSize.Unspecified
        )
    ) {
        val scope = rememberCoroutineScope()
        val state = remember { GameState(MongoStorageAsync(database), scope) }
        MaterialTheme {
            CheckersMenu(state::start,state::refresh,state::showTargets,state)
            if (state.openDialogName)
                DialogName(onCancel = { state.closeDialog() }) { state.start(it) }
            state.message?.let {
                DialogMessage(it) { state.messageAck() }
            }
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    boardView(state,state::play)
                    outsideView(state)
                }
            }
        }
    }
}

