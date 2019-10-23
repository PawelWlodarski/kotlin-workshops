package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.*

fun main() = runBlocking {
    val job= async {
        coroutineScope {

            launch(Dispatchers.Default) {
                println("block 1")
                launch {
                    println("block 11 ${threadName()}")
                    delay(1000)
                }
            }
        }


        coroutineScope{
            launch {
                delay(500)
                println("block2")
            }

            launch{
                println("block3")
            }
        }

        launch {
            delay(500)
            println("main context child ended")
        }
    }

    job.cancel()


    delay(1000)
    println("end")
}
