package lodz.jug.kotlin.coroutines.bonus

import kotlinx.coroutines.*
import lodz.jug.kotlin.coroutines.displayThread
import java.util.concurrent.Executors

fun main() {

    val mainDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val yourMainScope = CoroutineScope(mainDispatcher)

    val blockingDispatcher = Executors.newFixedThreadPool(20).asCoroutineDispatcher()


    val job=yourMainScope.launch {
        displayThread("this is running in your main scope in the main context")

        val result=withContext(blockingDispatcher){
            displayThread("context switched but scope is the same")
            Thread.sleep(1000)
            69
        }

        displayThread("this is running in your main scope in the main context : result=$result")

        val result2=coroutineScope {
            displayThread("Main context, different scope, main coroutine")

            launch {
                displayThread("Main context, different scope, sub coroutine")

                withContext(Dispatchers.Default){
                    displayThread("Default context, different scope, sub coroutine")
                }

            }

            1
        }

        val result3=coroutineScope {
            val r1=async(blockingDispatcher) {
                displayThread("async1")
                Thread.sleep(500)
                3
            }

            val r2=async(blockingDispatcher) {
                displayThread("async2")
                Thread.sleep(500)
                2
            }

            r1.await()+r2.await()
        }

        displayThread("main scope, main context, main coroutine : result=${result + result2 + result3}")

    }

    runBlocking {
        displayThread("blocking and waiting")
        job.join()
    }

    blockingDispatcher.close()
    mainDispatcher.close()
//    yourMainScope.cancel()
}