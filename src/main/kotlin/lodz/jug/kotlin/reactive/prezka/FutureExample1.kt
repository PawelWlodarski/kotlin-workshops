package lodz.jug.kotlin.reactive.prezka

import java.util.concurrent.CompletableFuture

fun main() {
    val asyncComputation = CompletableFuture.supplyAsync {
        Example1.readUserId()
    }.thenApplyAsync { id ->
        Example1.findUser(id)
    }.thenApply { user ->
        Example1.uiRepresentation(user)
    }.whenComplete { text, _ ->
        println(text)
    }

    asyncComputation.join()
}

object Example1{
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
