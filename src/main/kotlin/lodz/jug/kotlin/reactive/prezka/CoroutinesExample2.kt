package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.*


fun main() {

    val job=GlobalScope.launch {
        val id=Example2.readUserId()
        val user=Example2.findUser(id)
        val textToDisplay=Example2.uiRepresentation(user)
        println("in ${Thread.currentThread().name} $textToDisplay")
    }

    runBlocking {
        println("waiting in Thread : ${Thread.currentThread().name}")
        job.join()
    }
}


object Example2{
    suspend fun readUserId(): Int = coroutineScope {
            Thread.sleep(1000)
            1
    }

    suspend fun findUser(id: Int): User  = coroutineScope{
        Thread.sleep(1000)
        User(id, "Roman")
    }

    fun uiRepresentation(u: User): String = "User[id=${u.id}, name=${u.name}]"


    data class User(val id: Int, val name: String)
}