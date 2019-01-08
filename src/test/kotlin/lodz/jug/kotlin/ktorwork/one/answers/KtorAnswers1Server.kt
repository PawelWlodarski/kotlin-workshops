package lodz.jug.kotlin.ktorwork.one.answers

import arrow.core.Try
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


object KtorAnswers1Server {

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
    routing {
        post("/addNumber/{number}") {
            fun handleError(e: Throwable) = Pair(HttpStatusCode.BadRequest, "unable to retrieve parameter : $e")
            fun handleSuccess(n: Int): Pair<HttpStatusCode, String> {
                Exercise1AnswerDatabase.addToDb(n)
                return Pair(HttpStatusCode.OK, "added number $n")
            }

            val (status, message) = Try {
                call.parameters["number"]!!.toInt()
            }.fold(::handleError, ::handleSuccess)

            call.respondText(text = message, status = status)
        }


        delete("/all"){
            Exercise1AnswerDatabase.clear()
            call.respondText("all data removed!!!")
        }

        get("/numbers"){
            call.respond(Exercise1AnswerDatabase.selectAll())
        }

        put("/reset"){
            val data=call.receive<Exercise1AnswerDatabase.Numbers>()
            Exercise1AnswerDatabase.reset(data)
            call.respondText("database reset")
        }


    }


    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(TextContent("try again later", ContentType.Text.Plain, it))
        }
    }

    install(ContentNegotiation){
        gson {}
    }
}