package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    val job= GlobalScope.launch {
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


object Example3{
    fun readUserId(): Int {
        Thread.sleep(1000)
        return 1
    }

    fun findUser(id: Int): User  {
        Thread.sleep(1000)
        return User(id, "Roman")
    }

    fun uiRepresentation(u: User): String = "User[id=${u.id}, name=${u.name}]"


    data class User(val id: Int, val name: String)
}