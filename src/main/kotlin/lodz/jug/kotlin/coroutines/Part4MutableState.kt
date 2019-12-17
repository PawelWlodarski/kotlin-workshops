package lodz.jug.kotlin.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.sync.Mutex
import lodz.jug.kotlin.coroutines.Example3Actor.CounterValue
import lodz.jug.kotlin.coroutines.Example3Actor.Example3Protocol.GetCounter
import lodz.jug.kotlin.coroutines.Example3Actor.Example3Protocol.IncrementCounter
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    example1JustLocalVariable()
//    example2MutexWithSuspension()
//    example3Actor()
//    example4FailedProducersAndActors()
}

fun example1JustLocalVariable() = runBlocking {
    var counter = 0
    val atomicCounter = AtomicInteger()

    suspend fun incrementation(id: String) {
        repeat(1000) {
            counter++
            val value = atomicCounter.incrementAndGet()
            displayThread("$id : $counter, values=$value")
            delay(1)
        }
    }


    val job = launch(Dispatchers.Default) {
        incrementation("c1")
    }

    //EXERCISE - comment this one and check if one coroutine changes state unpredictably
    val job2 = launch(Dispatchers.Default) {
        incrementation("c2")
    }

    job.join()
    job2.join()
    displayThread("$counter")
}

fun example2MutexWithSuspension() = runBlocking {
    var counter = 0

    val lock = Object()
    val mutex = Mutex()

    suspend fun incrementation(id: String) {
        repeat(1000) {
            //            synchronized(lock){}  //attempt 1 - synchronized block
            //            mutex.withLock {} //attempt 2, only incrementation, withLock vs lock , loanPattern
            counter++

            displayThread("$id : $counter")
            delay(1)


        }
    }


    val job = launch(Dispatchers.Default) {
        incrementation("c1")
    }

    incrementation("main")

    job.join()
    displayThread("$counter")
}

object Example3Actor {
    sealed class Example3Protocol {
        object IncrementCounter : Example3Protocol()
        class GetCounter(val sender: SendChannel<CounterValue>) : Example3Protocol()
    }

    data class CounterValue(val c: Int)
}

fun example3Actor() = runBlocking {

    val counterActor = actor<Example3Actor.Example3Protocol> {
        var counter = 0

        for (msg in channel) {
            when (msg) {
                is IncrementCounter -> counter++
                is GetCounter -> msg.sender.send(CounterValue(counter))
            }
        }
    }

    coroutineScope {
        repeat(1000) {
            launch(Dispatchers.Default) {
                delay(1000L - it)
                counterActor.send(IncrementCounter)
            }
        }
    }

    val responseChannel = Channel<CounterValue>()

    val job = launch {
        val result = responseChannel.receive()
        displayThread("actor call result : $result")
    }

    counterActor.send(GetCounter(responseChannel))
    job.join()

    responseChannel.close()
    counterActor.close()
}


fun example4FailedProducersAndActors() = runBlocking {
    val producer1 = produce {
        for (i in 1..100) {
            delay(20)
            send(i)
        }
    }



    val producer2 = produce {
        delay(40)
        for (i in 'a'..'z') {
//            delay(40)  -- why it is needed here?
            send(i)
        }
    }

    val clock = ticker(delayMillis = 100)

    val actor=actor<Unit> {

        var buffer = ""

        whileSelect {

            producer1.onReceiveOrNull {
                displayThread("from producer 1 : $it , $buffer")
                buffer += it
                it!=null
            }

            producer2.onReceiveOrNull {
                displayThread("from producer 2 : $it, $buffer")
//                if(it == null) producer2.cancel() //why it is not working?
                buffer += it
                it!=null //can we cancel producer here?
//                true // what happen if always true?
            }
            clock.onReceive {
                displayThread("buffer($buffer)")
                buffer = ""
                true
            }
        }
    }

    delay(2000)
    actor.close()
//    producer1.cancel() // why this one is needed to terminate program?
    producer2.cancel()
    clock.cancel()


}