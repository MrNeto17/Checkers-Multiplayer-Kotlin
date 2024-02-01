package isel.leic.tds.checkers.ui

import isel.leic.tds.checkers.board.Checkers
import isel.leic.tds.checkers.storage.Storage

abstract class Command {
    /**
     * Operation to be performed in the game with the indicated arguments.
     * @param game Actual game state.
     * @param args Arguments passed to command.
     * @return The game with the changes made or null if it is to end.
     */
    abstract fun action(game: Checkers?, args: List<String>): Checkers?

    /**
     * Presentation of command result.
     * @param game Actual game state.
     */
    fun show(game: Checkers, st: Storage) {
        if(game.gameName != null) println(toStringBoard(game,st.getTurn(game.gameName)))
    }

    open val argsSyntax = ""
}

fun getCommands(st: Storage) = mapOf(
    "START" to object : Command() {
        override fun action(game: Checkers?, args: List<String>) = startAction(game, args, st)
        override val argsSyntax = "<gameName>"
    },
    "PLAY" to object : Command() {
        override fun action(game: Checkers?, args: List<String>): Checkers {
            return playAction(game, args, st)
        }
        override val argsSyntax = "<position>"
    },
    "REFRESH" to object : Command() {
        override fun action(game: Checkers?, args: List<String>): Checkers {
            return refreshAction(game, st)
        }
    },
    "EXIT" to object : Command() {
        override fun action(game: Checkers?, args: List<String>): Nothing? {
            checkNotNull(game) { "Game not started yet" }
            game.gameName?.let { st.removeDoc(it) }
            return null
        }
    }
)

