package com.idemia.asyncintro.demo.koroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

fun main() {
    aLotOfCoroutines()
//    aLotOfThreads()
}


/**
 * will work
 */
fun aLotOfCoroutines() = runBlocking {
    repeat(100_000) {counter ->
        // launch a lot of coroutines
        launch {
            delay(1000L)
            println("$counter in ${Thread.currentThread().name} " )
        }
    }
}



/**
 * out of memory
 */
fun aLotOfThreads() =
    repeat(100_000) {counter ->
        // launch a lot of coroutines
        thread{
            Thread.sleep(1000L)
            println("$counter in ${Thread.currentThread().name} " )
        }
}

