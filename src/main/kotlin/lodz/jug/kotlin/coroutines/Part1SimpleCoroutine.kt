package lodz.jug.kotlin.coroutines

import kotlinx.coroutines.*
import lodz.jug.kotlin.reactive.croutines.displayThread
import lodz.jug.kotlin.reactive.croutines.withTimeMeasurement
import java.util.concurrent.Executors

fun main() {
    example1BlockingMainThread()
//    example2DontBlockMainThread()
//    example3ParallelExecution()
//    example4ResourceLeak()
//    example5StopCoroutine()
//    example6SuspendedFunctions()
//    example7SuspendedFunctionsWithCustomThread()
}


/**
 * INFO : No coroutines here , main thread is blocked, UI freezes.
 */
fun example1BlockingMainThread() {
    withTimeMeasurement("Blocking main thread example") {
        val img1=ImageRepoExample1.process(1)
        val img2=ImageRepoExample1.process(2)
        ImageRepoExample1.save(img1,img2)
    }
}

/**
 * INFO : We are running blocking operation on the coroutine lanuched on IO thread.
 * * main thread is not blocked
 * * remove Dispatchers.IO and check what will happen
 */
fun example2DontBlockMainThread() {
    val job=GlobalScope.launch(Dispatchers.IO) {//TODO: remove dispatchers io and check what will happen
        withTimeMeasurement("Not blocking main thread") {
            val img1=ImageRepoExample1.process(1)
            val img2=ImageRepoExample1.process(2)
            ImageRepoExample1.save(img1,img2)
        }
    }

    displayThread("main thread unblocked")
    Thread.sleep(200)
    displayThread("main thread still unblocked")

    runBlocking {
        job.join()
    }
}


/**
 * INFO : Both Images are generated at the same time
 *
 */
fun example3ParallelExecution() {
    val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    withTimeMeasurement("Parallel execution"){
        val img1: Deferred<Image> =GlobalScope.async{
            displayThread("async block1")
            ImageRepoExample1.process(1)
        }

        ///here we are changing easily execution thread context
        val img2=GlobalScope.async{
            displayThread("async block2 start")
            withContext(workersDispatcher){
                displayThread("async block2 switched context")
                ImageRepoExample1.process(2)
            }
        }

        val job=GlobalScope.launch {
            ImageRepoExample1.save(img1.await(),img2.await())
        }


        displayThread("main thread unblocked")
        Thread.sleep(200)
        displayThread("main thread still unblocked")

        runBlocking {
            job.join()
        }
    }

    workersDispatcher.close()
}

/**
 * INFO : After firs job fails whole calculation should be cancelled but the second job is still using resources
 *
 */
fun example4ResourceLeak(){
    val img1:Deferred<Image> = GlobalScope.async{
        println("job 1 fails")
        throw RuntimeException("error")
    }

    val img2=GlobalScope.async{
        ImageRepoExample1.process(2)
    }

    val job=GlobalScope.launch {
        ImageRepoExample1.save(img1.await(),img2.await())
    }


    displayThread("main thread unblocked")
    Thread.sleep(200)
    displayThread("main thread still unblocked")

    runBlocking {
        job.join()
    }

    Thread.sleep(2000)  //Why sleep is needed here?
}

/**
 * INFO : Cancelling is cooperative and can be only triggered in a "suspension point".
 * // experiment by switching between Thread.sleep and delay - what is the difference between those two?
 *
 */
fun example5StopCoroutine(){

    val job=GlobalScope.launch(Dispatchers.IO) {
        repeat(100){iteration ->
            println("iteration number : $iteration")
            Thread.sleep(200)
//            delay(200)
        }
    }


    runBlocking {
        delay(900)
        job.cancelAndJoin()
    }
}


fun example6SuspendedFunctions(){

    println("turn on visualvm and observe threads")
    readLine()

    suspend fun blockingFunctionA():Int= withContext(Dispatchers.IO){
        displayThread("A: sleeping : ")
        Thread.sleep(5000)
        1
    }

    fun standardFunction():Int{
        Thread.sleep(2000)  // check what will happen when function blocks in the main context
        return 2
    }

    suspend fun blockingFunC()=withContext(Dispatchers.IO){
        displayThread("C: sleeping : ")
        Thread.sleep(3000)
        3
    }

    suspend fun dependandFunD():Int {
        displayThread("D: waiting in suspension for C : ")
        return 4 + blockingFunC()
    }

    runBlocking {
        displayThread("runBlocking in")
        val result= blockingFunctionA() + standardFunction() + dependandFunD()
        println("result after all suspensions is : $result")
    }


}

fun example7SuspendedFunctionsWithCustomThread(){
    val singleThread = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    suspend fun blockingFunctionA():Int= withContext(Dispatchers.IO){
        Thread.sleep(500)
        1
    }

    fun standardFunction():Int {
//        Thread.sleep(2000)    //run program, observe results and then change context to IO
        return 2
    }

    suspend fun blockingFunC()=withContext(Dispatchers.IO){
        Thread.sleep(300)
        3
    }

    suspend fun dependandFunD():Int {
        return 4 + blockingFunC()
    }

    val jobs=(1 .. 10).map{iterationNumber ->
        Thread.sleep(200)
        GlobalScope.launch(singleThread) {
            println("starting iteration $iterationNumber")
            val result= standardFunction() + blockingFunctionA() + dependandFunD()
            println("iteration $iterationNumber : result after all suspensions is : $result")
        }
    }

    runBlocking {
        jobs.forEach{it.join()}
    }

    singleThread.close()
}


typealias Image = String
typealias ImageData = Int

object ImageRepoExample1 {

    fun process(data: ImageData): Image {
        displayThread("start generating image data for $data")
        Thread.sleep(1000)
        displayThread("image data generated $data")
        return "image$data"
    }

    fun save(vararg images: Image) {
        displayThread("start saving images :  ${images.joinToString() }")
        Thread.sleep(1000)
        displayThread("images saved ${images.joinToString() }")
    }

}