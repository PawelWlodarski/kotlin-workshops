package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun main() {
    val calc1: Deferred<Int> =GlobalScope.async {
        println("calculation 1 in ${threadName()}")
        Thread.sleep(1000)
        2
    }


    val calc2: Deferred<Int> =GlobalScope.async {
        Thread.sleep(1000)
        3
    }

    runBlocking {
        val result=calc1.await() + calc2.await()
        println("$result in ${threadName()}")
    }
}