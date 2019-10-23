package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.*

fun main() {
    val calc1: Deferred<Int> =GlobalScope.async {
        println("calculation 1 in ${threadName()}")
        delay(10000)
        println("calculation 1 ended in ${threadName()}")
        2
    }


    val calc2: Deferred<Int> =GlobalScope.async {
        throw RuntimeException("makabra...")
        delay(5000)
        3
    }

    runBlocking {
        val result=calc1.await() + calc2.await()
        println("$result in ${threadName()}")
    }
}