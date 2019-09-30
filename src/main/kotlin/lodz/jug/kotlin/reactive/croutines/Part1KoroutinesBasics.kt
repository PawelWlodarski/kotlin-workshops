package lodz.jug.kotlin.reactive.croutines

import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes


fun main() {
    example1BlockingMainThread()
//    example2RunInGlobalScope()
//    example3WaitingForJob()
//    example4SerialAndParallelProcessing()
//    example5SerialButWithSuspension()
//    example6Debug()
//    example7Scopes()
//    example7Scopes2()
}


/**
 * INFO : No coroutines here , main thread is blocked, UI freezes.
 */
fun example1BlockingMainThread() {
    withTimeMeasurement("Blocking main thread example") {
        ImageRepoExample1.save("image1.jpg")
    }
}

/**
 * INFO : notice that main section ends before image is actually saved
 * that is why we need Thread.sleep
 */
fun example2RunInGlobalScope() {
    withTimeMeasurement("Saving image in global scope") {
        GlobalScope.launch {
            ImageRepoExample1.save("image1.jpg")
        }
    }

    Thread.sleep(2000)
}

/**
 * INFO : Example shows how to compose two coroutines started in different scopes using
 * Job abstraction. Global Scope is indepenedent and int the first we example outer coroutine doesn't wait for it
 */
fun example3WaitingForJob() = runBlocking {
    //just run blocking without blocking
    withTimeMeasurement("Saving image inside runBlocking", isActive = true) {
        GlobalScope.launch {
            ImageRepoExample1.save("image1.jpg")
        }

    }

    //run blocking with waiting for a job
    withTimeMeasurement("Joining coroutine job", isActive = false) {
        runBlocking {
            val job = GlobalScope.launch {
                ImageRepoExample1.save("image2.jpg")
            }

            job.join() //TASK :  comment this line and check what happen, then remove global scope
        }
    }
}

/**
 * INFO: Observe Threads in both examples
 */
fun example4SerialAndParallelProcessing() {
    withTimeMeasurement("serialProcessing", isActive = true) {
        runBlocking {
            launch {
                val img1 = ImageRepoExample1.process(1)
                ImageRepoExample1.save(img1)
            }

            launch {
                val img2 = ImageRepoExample1.process(2)
                ImageRepoExample1.save(img2)
            }

        }
    }

    withTimeMeasurement("parallelProcessing", isActive = false) {
        runBlocking {
            launch(Dispatchers.Default) {
                val img1 = ImageRepoExample1.process(1)
                ImageRepoExample1.save(img1)
            }

            launch(Dispatchers.Default) {
                val img2 = ImageRepoExample1.process(2)
                ImageRepoExample1.save(img2)
            }

        }
    }
}

/**
 * INFO: this should be one second faster than serial example 4, why?
 * INFO2: After that show what compiler generates
 */
fun example5SerialButWithSuspension() {
    withTimeMeasurement("serialProcessing", isActive = true) {
        runBlocking {
            launch {
                val img1 = ImageRepoExample1.process(1)
                SuspendingImageRepoExample1.save(img1)
            }

            launch {
                val img2 = ImageRepoExample1.process(2)
                SuspendingImageRepoExample1.save(img2)
            }

        }
    }
}


fun example6Debug() {
    //-javaagent:..../.m2/repository/org/jetbrains/kotlinx/kotlinx-coroutines-debug/1.3.1/kotlinx-coroutines-debug-1.3.1.jar
    System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
//    System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_OFF)
//    System.setProperty("kotlinx.coroutines.stacktrace.recovery", "false")
    DebugProbes.install()
    DebugProbes.dumpCoroutines()

    //INFO :  SHOW CoroutineName hierarchy
    withTimeMeasurement("serialProcessing", isActive = true) {
        runBlocking(CoroutineName("Parent_Coroutine")) {
            launch(CoroutineName("Coroutine_Image_1")) {
                val img1 = ImageRepoExample1.process(1)
                SuspendingImageRepoExample1.save(img1)
            }
            DebugProbes.dumpCoroutines()
            val job = launch(CoroutineName("Coroutine_Image_2")) {
                val img2 = ImageRepoExample1.process(2)
                SuspendingImageRepoExample1.save(img2)
            }

            DebugProbes.printJob(job)
        }
    }
}


/**
 * Observe order of execution,
 * External context doesn't wait for internal coroutines to end.
 * switch function declaration to CoroutineScope.go to see the difference
 */
fun example7Scopes() = runBlocking {

//    fun CoroutineScope.go(message: String, delay: Long) = launch {
    fun go(message: String, delay: Long) = launch {
        println("$message - 1")
        delay(delay)
        println("$message - 2")
    }

    launch {
        delay(200)
        println("blocking scope")
    }

    coroutineScope {
        println("scope1-start")
        go("launch1", 500)
        go("launch2", 700)
        println("scope1-end")
    }


    coroutineScope {
        println("scope2-start")
        go("launch3", 300)
        go("launch4", 900)
        println("scope2-end")
    }

    println("runBlocking End")
}

/**
 * in second example we are not using 'go' method but are launching logic directly, what is the difference?
 */
fun example7Scopes2() = runBlocking {

    launch {
        delay(200)
        println("\"blocking scope\"")
    }

    coroutineScope {
        println("scope1-start")
        launch {
            println("${"launch1"} - 1")
            delay(500)
            println("${"launch1"} - 2")
        }
        launch {
            println("${"launch2"} - 1")
            delay(700)
            println("${"launch2"} - 2")
        }
        println("scope1-end")
    }


    coroutineScope {
        println("scope2-start")
        launch {
            println("${"launch3"} - 1")
            delay(300)
            println("${"launch3"} - 2")
        }
        launch {
            println("${"launch4"} - 1")
            delay(900)
            println("${"launch4"} - 2")
        }
        println("scope2-end")
    }

    println("runBlocking End")
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

    fun save(i: Image) {
        displayThread("start saving image $i")
        Thread.sleep(1000)
        displayThread("image saved $i")
    }

}

object SuspendingImageRepoExample1 {

    suspend fun process(data: ImageData): Image {
        displayThread("start generating image data for $data")
        delay(1000)
        displayThread("image data generated $data")
        return "image$data"
    }

    // INFO : Without word 'suspend' we can not call 'delay'
    suspend fun save(i: Image) {
        displayThread("start saving image $i")
        delay(1000)
        displayThread("image saved $i")
    }

}