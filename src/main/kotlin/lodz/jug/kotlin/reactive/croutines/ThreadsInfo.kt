package lodz.jug.kotlin.reactive.croutines

fun displayThread(message:String) = println("thread : ${Thread.currentThread().name} : $message")