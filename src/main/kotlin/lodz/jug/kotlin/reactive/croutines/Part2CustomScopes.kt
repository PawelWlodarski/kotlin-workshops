package com.idemia.asyncintro.demo.koroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger


/**
 * Factory used to generate custom names for threads
 */
val factory = object : ThreadFactory {
    private val counter = AtomicInteger()

    override fun newThread(r: Runnable): Thread =
        Thread(r).apply {
            val counterValue = counter.getAndIncrement()
            name = "mojThread-$counterValue"
        }

}


private fun displayThread(message: String) {
    println("$message In Thread : " + Thread.currentThread().name)
}

fun main() {
    val customThreadPool = Executors.newFixedThreadPool(4, factory)

    /**
     * custom scope in which we will start our coroutines
     */
    val customScope = CoroutineScope(customThreadPool.asCoroutineDispatcher())


    //main coroutine
    customScope.launch {
        displayThread("inside custom scope")
        //child launched in the same coroutine scope
        customScope.launch {
            displayThread("inside inner scope")
        }
        displayThread("inside custom scope2")

        //child scope
        coroutineScope {
            displayThread("inside myThreadFactory scope")
            //coroutine launched in the child scope
            launch {
                displayThread("inner launch inside myThreadFactory scope")
            }
            displayThread("after myThreadFactory scope")
        }
        displayThread("inside custom scope3")
    }

    displayThread("end of program")
//    customScope.cancel()  //uncomment and check result
//    mainThreadPool.shutdownNow()  //uncomment and check result
    //show how to use job toi cancel this
}