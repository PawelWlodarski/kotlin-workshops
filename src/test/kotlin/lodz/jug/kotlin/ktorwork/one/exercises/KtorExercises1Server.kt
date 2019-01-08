package lodz.jug.kotlin.ktorwork.one.exercises

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


object KtorExercises1Server {

    const val serverPort = 8080

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(Netty, port = serverPort, module = Application::exercise1).start(wait = true)
    }
}

object Exercise1AnswerDatabase {
    private var database: List<Int> = emptyList()

    data class Numbers(val numbers:List<Int>)

    fun addToDb(number: Int) {
        database += number
    }

    fun  selectAll() = Numbers(database)

    fun clear() {database =  emptyList()}

    fun reset(ns:Numbers) {
        database = ns.numbers
    }
}

fun Application.exercise1() {
        TODO()  // EXERCISE
}