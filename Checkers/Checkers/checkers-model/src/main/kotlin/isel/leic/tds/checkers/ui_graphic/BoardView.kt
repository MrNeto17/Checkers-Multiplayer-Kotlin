package isel.leic.tds.checkers.ui_graphic

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import isel.leic.tds.checkers.board.*

const val CELL_SIZE = 50

@Composable
fun boardView(state: GameState, onClick: (Square) -> Unit) {
    Column {
        repeat(BOARD_DIM + 1) { row ->
            if (row != 0) Spacer(Modifier.height(1.dp))
            Row {
                repeat(BOARD_DIM) { col ->
                    if (col == 0) {
                        Text(
                            if(state.game.player == Player.BLACK) {
                                if (row == 0) " "
                                else (row).toString()
                            }
                            else {
                                if (row == 0) " "
                                else (ROW_DIM + 1 - row).toString()
                            },
                            modifier = Modifier.align(Alignment.CenterVertically).size(25.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (col != 0) Spacer(Modifier.width(1.dp))
                    if (row != 0) {
                        val square =
                            if(state.game.player == Player.BLACK)
                                Square((BOARD_DIM - row).indexToRow(), (col).indexToColumn())
                            else
                                Square((row - 1).indexToRow(), (col).indexToColumn())
                        if (square.playable) {
                            EmptyCellView(Color.White)
                        } else {
                            val color = if (state.allTargets.any {
                                    it.second.row.index == square.row.index && it.second.column.index == square.column.index
                                }) {
                                Color.Red
                            } else if (state.fromPos == square) {
                                Color.Red
                            } else {
                                Color.Black
                            }
                            if(state.game.player == Player.BLACK)
                                CellView(color, state, BOARD_DIM - row, col, onClick)
                            else
                                CellView(color, state, row - 1, col, onClick)
                        }
                    } else {
                        Text(
                            ('A' + col).toString(),
                            Modifier.size(CELL_SIZE.dp).padding(top = 20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CellView(color: Color, state: GameState, row: Int, col: Int, onClick: (Square) -> Unit) {
    val square = Square(row.indexToRow(), col.indexToColumn())
    val modifier = Modifier.size(CELL_SIZE.dp)
    Box(
        if (state.game.gameName != null) {
            modifier.clickable {
                if (state.fromPos == square) {
                    state.allTargets = emptyList()
                    state.fromPos = null
                } else {
                    onClick.invoke(square)
                }
            }.background(color)
        } else {
            modifier.background(color)
        }
    ) {
        when (state.game.board!!.boardArr[row][col]) {
            Representation.BLACK -> Image(painterResource("piece_b.png"), "piece_b", Modifier.fillMaxSize())
            Representation.WHITE -> Image(painterResource("piece_w.png"), "piece_w", Modifier.fillMaxSize())
            Representation.KING_B -> Image(painterResource("piece_bk.png"), "piece_b", Modifier.fillMaxSize())
            Representation.KING_W -> Image(painterResource("piece_wk.png"), "piece_w", Modifier.fillMaxSize())
            else -> modifier.background(Color.Black)
        }
    }
}
@Composable
fun EmptyCellView(color: Color) {
    val modifier = Modifier.size(CELL_SIZE.dp)
    Box(
        modifier.background(color)
    )
}
