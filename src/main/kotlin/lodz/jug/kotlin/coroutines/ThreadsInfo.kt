package lodz.jug.kotlin.coroutines

import kotlin.system.measureTimeMillis

fun displayThread(message: String) = println("thread : ${Thread.currentThread().name} : $message")


fun withTimeMeasurement(title: String, isActive: Boolean = true, code: () -> Unit) {
    if (!isActive) return

    displayThread("start code")
    val timePassed = measureTimeMillis {
        code()
    }
    displayThread("end code")

    println("operation in '$title' took $timePassed ms")
}


