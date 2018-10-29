package lodz.jug.kotlin.reactive.croutines

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>) {
    example1CancelSimpleJob()
}



fun example1CancelSimpleJob() = runBlocking{
    val job=launch{
        repeat(1000){i ->
               displayThread("example 1 in repetition $i")
               delay(500)
        }
    }

    delay(1300)
    displayThread("example 1 after second delay, just before canceling")
    job.cancel()
    job.join()
    displayThread("example after canceling")
}