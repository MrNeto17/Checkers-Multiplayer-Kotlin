package isel.leic.tds.checkers.ui

typealias LineCommand = Pair<String,List<String>>

fun readCommand(): LineCommand {
    while (true) {
        print("> ")
        return readLine()?.parseCommand() ?: continue
    }
}

fun String.parseCommand(): LineCommand? {
    val words = trim().split(' ').filter { it.isNotBlank() }
    if (words.isEmpty()) return null
    val name = words[0].uppercase()
    val args = words.drop(1)
    return name to args
}
