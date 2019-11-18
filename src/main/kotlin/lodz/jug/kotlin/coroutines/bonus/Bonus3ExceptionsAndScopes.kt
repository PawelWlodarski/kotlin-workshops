package lodz.jug.kotlin.coroutines.bonus

import kotlinx.coroutines.*

fun main() {
//    example1TryCatchInMain()
    example2TryCatchInScope()
}


private fun example1TryCatchInMain() {
    runBlocking {

        lateinit var job: Job

        try {
            job = launchCoroutine()
        } catch (e: RuntimeException) {
            println("caught exception $e")
        }

        job.join()
    }
}

private fun example2TryCatchInScope() {
    runBlocking {

        lateinit var job: Job

        try {
            coroutineScope {
                job = launchCoroutine()
            }
        } catch (e: RuntimeException) {
            println("caught exception $e")
        }

        coroutineScope {
            try {
                withContext(SupervisorJob()){
                    job = launchCoroutine()
                }
            } catch (e: RuntimeException) {
                println("caught exception $e")
            }
        }

        job.join()
    }
}

private fun CoroutineScope.launchCoroutine(): Job = launch {

    delay(500)
    throw RuntimeException("from child coroutine")

}


