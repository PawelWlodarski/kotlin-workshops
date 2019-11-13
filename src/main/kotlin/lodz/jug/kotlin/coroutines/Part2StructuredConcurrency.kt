package lodz.jug.kotlin.coroutines

import kotlinx.coroutines.*
import lodz.jug.kotlin.reactive.croutines.displayThread
import java.lang.AssertionError
import java.lang.IllegalArgumentException
import java.util.concurrent.Executors

fun main() {
    example1CancelingStructures()
//    example2CancelChild()
//    example3Scopes()
//    example4MoreScopes()
//    example5ExceptionWithinScope()
//    example6CatchToEarly()
//    example7Supervision()
//    example7SupervisionSample2()
 //   example7SupervisionSample3()
//    example7SupervisionSample4()
}

fun example1CancelingStructures() = runBlocking {

    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    val job = launch(workersDispatcher) {
        displayThread("firs level : ")
        delay(300)

        launch {
            displayThread("second level : ")
            delay(200)
            launch {
                displayThread("third level : ")
                delay(1000)
                displayThread("third level end")
            }
            displayThread("second level end")
        }
        displayThread("first level end")
    }

    delay(700)
//    job.cancel()  //test :
    job.join()

    workersDispatcher.close()  //what will happen if we close before job is finished?
}

fun example2CancelChild() = runBlocking {
    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    lateinit var job: Job

    launch(workersDispatcher) {
        displayThread("firs level : ")
        delay(300)
        job = launch {
            displayThread("second level : ")
            delay(200)
            launch {
                displayThread("third level : ")
                delay(1000)
                displayThread("third level end")
            }
            displayThread("second level end")
        }
        delay(500)
        displayThread("first level end")
    }

    delay(400)
//    job.cancel()
    job.join()

    workersDispatcher.close()
}

fun example3Scopes() = runBlocking {
    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    val job = launch(workersDispatcher) {
        displayThread("firs level : ")
        delay(300)
        coroutineScope {
            displayThread("second level : ")
            delay(200)
            launch {
                displayThread("third level : ")
                delay(1000)
                displayThread("third level end")
            }
            displayThread("second level end")
        }
        delay(500)
        displayThread("first level end") //this will be the last one
    }

    delay(400)
//    job.cancel()
    job.join()

    workersDispatcher.close()
}


fun example4MoreScopes() = runBlocking {
    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    val job = launch(workersDispatcher) {
        displayThread("firs level : ")
        delay(300)

        coroutineScope {
            displayThread("second level first scope : ")
            delay(200)
            displayThread("second level first scope end: ")
        }

        coroutineScope {
            displayThread("second level second scope: ")
            delay(200)
            launch {
                displayThread("third level second scope: ")
                delay(1000)
                displayThread("third level second scope end")
            }
            displayThread("second level second scope: end")
        }
        delay(500)
        displayThread("first level after child finished") //this will be the last one
    }

    delay(400)
//    job.cancel()
    job.join()

    workersDispatcher.close()
}


fun example5ExceptionWithinScope() = runBlocking {
    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    val job = launch(workersDispatcher) {
        displayThread("firs level : ")
        delay(300)

        coroutineScope {
            displayThread("second level first scope : ")
            delay(200)
            displayThread("second level first scope end: ")
        }

        coroutineScope {
            displayThread("second level second scope: ")
            delay(200)
            launch(Dispatchers.IO) {
                displayThread("failing third level second scope: ")
                delay(100)
                throw RuntimeException("failing third  level exception")
                delay(1000)
                displayThread("failing third level second scope end")
            }

            launch {
                try {
                    displayThread("working third level second scope")
                    delay(500)
                    displayThread("working third level second scope end")
                } catch (e: Exception) {
                    displayThread("working third level second scope : exception caught")
                    println("exception  : $e")
                } finally {
                    displayThread("working third level second scope : finally")
                }
            }
            displayThread("second level second scope: end")
        }
        delay(500)
        displayThread("first level after child finished") //this will be the last one
    }

    delay(400)
    job.invokeOnCompletion { exception ->
        println("job ended with an exception :  ${exception?.message}")
        workersDispatcher.close()  //EXERCISE : here you can close resources
    }
    job.join()


    workersDispatcher.close()  //EXERCISE : will it be closed?
    delay(500)
}


fun example6CatchToEarly() = runBlocking {
    suspend fun someFunction(id: Int): Job = launch(Dispatchers.IO) {

        try {
            delay(2000)
//        } catch (e: Exception) {    //Check the difference between those two catch clauses
        } catch (e: IllegalArgumentException) {
            displayThread("exception  : $e  in coroutine : 1")
        }

        displayThread("in coroutine $id")

    }

    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    launch(workersDispatcher) {
        displayThread("firs level : ")
        delay(300)

        lateinit var job: Job

        coroutineScope {
            displayThread("second level: ")
            delay(200)
            job = someFunction(1)
            someFunction(2)
            someFunction(3)
        }
        delay(100)
        job.cancel()
    }


    workersDispatcher.close()  //EXERCISE : will it be closed?
    delay(500)
}

fun example7Supervision() = runBlocking {

    displayThread("main starts")
    val job = launch(Dispatchers.Default) {
        displayThread("firs level starts")

        supervisorScope {
            displayThread("second level starts ")
            throw AssertionError("second level exception")
            displayThread("second level ends ")

        }

        delay(100)
        displayThread("firs level end")
    }

    job.join()
    displayThread("main ends")

}

fun example7SupervisionSample2() = runBlocking {

    displayThread("main starts")
    val job =
            supervisorScope {
//            coroutineScope {
                displayThread("firs level starts")
                val job=launch(Dispatchers.Default) {

                    displayThread("second level starts ")
                    throw AssertionError("second level exception")
                    displayThread("second level ends ")
                    delay(100)
                }
                displayThread("firs level end")
                job
            }

    job.join()
    displayThread("main ends")

}

//EXERCISE : show external scope error!!!
fun example7SupervisionSample3() = runBlocking {

    val supervisor = SupervisorJob()

    val customScope = CoroutineScope(Dispatchers.Default + supervisor)

    displayThread("main starts")
    val job =
            customScope.launch {  //EXERCISE : remove "customScope." and check the result
                //            coroutineScope {
                displayThread("firs level starts")
                launch(Dispatchers.Default) {

                    displayThread("second level starts ")
                    throw AssertionError("second level exception")
                    displayThread("second level ends ")
                    delay(100)
                }
                displayThread("firs level end")
            }

    job.join()
    displayThread("main ends")

}

fun example7SupervisionSample4() = runBlocking {

    val supervisor = SupervisorJob()

    val customScope = CoroutineScope(Dispatchers.Default + supervisor) //check without supervisor

    displayThread("main starts")
    val job =
            customScope.launch() {  //EXERCISE : remove "customScope." and check the result
                //            coroutineScope {
                displayThread("firs level starts")

                customScope.launch {
                    delay(100)
                    displayThread("first child start")
                    throw RuntimeException("first child exception")
                    displayThread("firs child end")
                }


                launch {
                    displayThread("second child start ")
                    delay(1000)
                    displayThread("second child end ")
                }

            }

    job.join()
    displayThread("main ends")
    delay(2000)

}