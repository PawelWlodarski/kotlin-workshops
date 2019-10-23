package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.*

val frameworkScope =  CoroutineScope(Dispatchers.Default)

fun main() {

    val jobs=(1 .. 10).map{
        runRequest("Request-$it")
    }

    runBlocking {
        jobs.forEach{it.join()}
    }
}


fun runRequest(r:String) = frameworkScope.launch{

    println("receiving request $r")

    val result=withContext(Dispatchers.IO){
        Thread.sleep(1000)
        println("processing $r in thread : ${threadName()}")
        Thread.sleep(500)

    }

    println("$r processed")

}

fun threadName() = Thread.currentThread().name