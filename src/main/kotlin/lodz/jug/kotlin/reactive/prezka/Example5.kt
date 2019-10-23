package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.*
import java.util.concurrent.Executors

fun main()  {

    val mainDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    val mainScope = CoroutineScope(mainDispatcher)

    val jobs= (1 .. 5).map {
        mainScope.launch {
            println("starting in main context in ${threadName()}")
            val result=readText()
            println("result is $result in ${threadName()}")
        }
    }


    runBlocking {
            jobs.forEach{it.join()}
    }

    mainDispatcher.close()

}

suspend fun readText(): String {
       // withContext(Dispatchers.IO) {
            val id = Example5.readUserId()
            val user = Example5.findUser(id)
            val textToDisplay = Example5.uiRepresentation(user)
            println("textToDisplay $textToDisplay in ${threadName()}")
            return textToDisplay
        }


object Example5 {


    fun readUserId(): Int {
        Thread.sleep(1000)
        return 1
    }

    fun findUser(id: Int): User {
        Thread.sleep(1000)
        return User(id, "Roman")
    }

    fun uiRepresentation(u: User): String = "User[id=${u.id}, name=${u.name}]"


    data class User(val id: Int, val name: String)
}