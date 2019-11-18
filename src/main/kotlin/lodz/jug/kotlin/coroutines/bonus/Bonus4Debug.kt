package lodz.jug.kotlin.coroutines.bonus

import kotlinx.coroutines.*
import kotlinx.coroutines.debug.DebugProbes
import lodz.jug.kotlin.coroutines.displayThread

fun main() {
    //-javaagent:..../.m2/repository/org/jetbrains/kotlinx/kotlinx-coroutines-debug/1.3.1/kotlinx-coroutines-debug-1.3.1.jar
    System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
//    System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_OFF)
//    System.setProperty("kotlinx.coroutines.stacktrace.recovery", "false")
    DebugProbes.install()

    println("empty coroutine dump")
    DebugProbes.dumpCoroutines()

    //INFO :  SHOW CoroutineName hierarchy
    runBlocking(CoroutineName("Parent_Coroutine")) {
        launch(CoroutineName("Coroutine_Image_1")) {
            val img1 = ImageRepoBonus4.process(1)
            SuspendingImageRepoBonus4.save(img1)
        }
        println("generating coroutine dump")
        DebugProbes.dumpCoroutines()
        val job = launch(CoroutineName("Coroutine_Image_2")) {
            val img2 = ImageRepoBonus4.process(2)
            SuspendingImageRepoBonus4.save(img2)
        }

        println("\n\n*** printing job details ***")
        DebugProbes.printJob(job)
    }
}

typealias Image = String
typealias ImageData = Int

internal object ImageRepoBonus4 {

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

internal object SuspendingImageRepoBonus4 {

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