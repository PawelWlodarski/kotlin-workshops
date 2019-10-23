package lodz.jug.kotlin.reactive.croutines

import kotlinx.coroutines.*

fun main() {
//    cancelOnlyWhenSuspends()
//    cancelTimeout()
    val r: Int? = cancelTimeout2()
    println(r)
}

/**
 * 'cancel' just sends a signal, cancellation occurs when flow is suspended
 */
fun cancelOnlyWhenSuspends() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)
            println("delay ends $i ...")
        }
    }
    "a".run {  }
    delay(1300L)
    println("before cancelling")
    job.cancel() // cancels the job
    println("after cancelling")
    delay(100)
    println("100 mills after cancelling")
    job.join() // waits for job's completion
    println("main: Now I can quit.")


    val dupa = try{2} catch(e:Exception) {0}
}

/**
 * throws an exception after given time
 */
fun cancelTimeout() = runBlocking {
    withTimeout(1300L) {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
}

fun cancelTimeout2() :Int? = runBlocking {
    withTimeoutOrNull(1300L){
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
        20
    }
}


