package lodz.jug.kotlin.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import java.util.concurrent.Executors

fun main() {
    example1SimpleChannel()
//    example2Producers()
//    example3SlowConsumer()
//    example4ProducersAndCoroutines()
//    example4ProducersAndCoroutines2()
//    example4ProducersAndCoroutines3()

}


fun example1SimpleChannel() = runBlocking {

    val customScope = CoroutineScope(Dispatchers.Default)

    //EXERCISE4 - check constructor
    val channel = Channel<String>()  // show constructor


    customScope.launch {
        displayThread("in custom scope")
        delay(100)
        channel.send("message1")
        delay(200)
        channel.offer("message2")
        delay(300)
        channel.send("message3")   //suspend
        channel.offer("message4")  //standard
        channel.offer("message5")
//        println(channel.offer("messageX")) //EXERCISE3 - check result
        channel.send("message6")
        channel.close()   //EXERCISE1 comment/uncomment
//        channel.send("message7") //EXERCISE2  - can you send after channel is closed?
    }


    //different scope
    val job = launch {
        for (message in channel) {
            displayThread("received $message")
        }
    }

    job.join()
    channel.close()

    delay(1000)
}

//No closing channel
fun example2Producers() = runBlocking {
    val customDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    val customScope = CoroutineScope(customDispatcher)

    val generator = produce(Dispatchers.Default) {
        displayThread("first producer")
        repeat(20) {
            delay(50)
            send(it)
        }
    }

    val squares = produce(Dispatchers.IO) {
        displayThread("second producer")
        delay(50)
        for (number in generator) if (number % 2 == 0) send(number * number)
    }


    val incrementedSquares = customScope.produce {
        displayThread("incrementing scope")
        for (e in squares) send(e + 1)
    }

    incrementedSquares.consumeEach {
        displayThread("consumer")
        println(it)
    }

    customDispatcher.close()

}


fun example3SlowConsumer() = runBlocking() {

    val channelType = Channel.RENDEZVOUS

    val channel = produce(capacity = channelType) {
        repeat(Int.MAX_VALUE) {
            delay(5)
            println("generating iteration : $it")
            val payload = "$it".repeat(20000)
            send(payload)
        }
    }

    launch {
        while (isActive) {
            delay(1000)
            val result = channel.receive()
            println(result)
        }
    }
}


fun example4ProducersAndCoroutines() = runBlocking {
    val channel = produce(Dispatchers.IO) {
        try {
            repeat(20) { iteration ->
                displayThread("iteration $iteration ")
                send("message$iteration")
                delay(100)
            }
        } catch (e: Exception) {
            displayThread(e.toString())
            throw e
        }

    }

    channel.receive()
    channel.receive()
    channel.receive()
    delay(1000)
    channel.receive()
    channel.receive()

    channel.cancel() //no close!!!

}

fun example4ProducersAndCoroutines2() {
    val channel = Channel<String>()  //no context
    val singleDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val singleScope = CoroutineScope(singleDispatcher)


    repeat(20) { externalLoop ->
        singleScope.launch {
            repeat(2) { internalLoop ->
                val msg = "message$externalLoop:$internalLoop"
                println("sending $msg")
                delay(10)
//                delay(10 * externalLoop.toLong()) //3
                channel.send(msg)
            }
//            channel.close() //3) or here ?
        }
    }

//    channel.close()  //1)should it be here?

    runBlocking {
        channel.consumeEach {
            println("consumed $it")
            delay(100)
        }
//        channel.close() //2) maybe here?
    }


    println("closing")
    channel.close()
    singleDispatcher.close()
    singleDispatcher.cancel()

}


fun example4ProducersAndCoroutines3() {
    val channel = Channel<String>()  //no context
    val singleDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    val singleScope = CoroutineScope(singleDispatcher)


    singleScope.launch {
        coroutineScope {
            repeat(20) { externalLoop ->
                launch {
                    repeat(2) { internalLoop ->
                        val msg = "message$externalLoop:$internalLoop"
                        println("sending $msg")
                        delay(10 * externalLoop.toLong())
                        channel.send(msg)
                    }
                }
            }
//            channel.cancel()  //Uncomment and check difference
//            channel.close()
        } //scope ends
        channel.close()
    }


    runBlocking {
        channel.consumeEach {
            println("consumed $it")
            delay(100)
        }
    }

    println("closing")
    singleDispatcher.close()

}