package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.*

fun main()  = runBlocking {
    val calc1: Deferred<Int> = async {
        println("calculation 1 in ${threadName()}")
        delay(10000)
        println("calculation 1 ended in ${threadName()}")
        2
    }


    val calc2: Deferred<Int> = async {
        throw RuntimeException("makabra...")
        delay(5000)
        3
    }

    val result=calc1.await() + calc2.await()
    println("$result in ${threadName()}")

}
