package lodz.jug.kotlin.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors

fun main() {
    example1JustFlow()
//    example2DifferentContext()
//    example3FlowAndCoroutinesCancellation()
//    example4IntermediateOperators()
//    example5ConstantFlow()
//    example6ConstantFlowWithChannel()
}


fun example1JustFlow() {


    val numbersFlow = flow {
        //No context needed, show how suspend is used in flow
        repeat(20) {
            delay(10)   //whe have context from flow
            displayThread("emitting $it")
            emit(it)
        }
    }

    runBlocking {
        numbersFlow.collect {
            displayThread("collected $it")
        }
    }

    runBlocking {
        launch(Dispatchers.IO) {
            numbersFlow.collect {
                displayThread("collected $it again")
            }
        }
    }
}


fun example2DifferentContext() {
    val f = flow {
        //this.withContext() <- check this
        kotlinx.coroutines.withContext(Dispatchers.Default) {
            //Flow invariant error
            repeat(20) {
                delay(20)
                displayThread("emitting $it")
                emit(it)
            }
        }
    }

    val f2 = flow {
        repeat(20) {
            delay(20)
            displayThread("emitting $it")
            emit(it)
        }
    }.flowOn(Dispatchers.Default)


    runBlocking {
        f.collect {
            displayThread("collected : $it")
        }
    }
}

fun example3FlowAndCoroutinesCancellation() {
    val flow: Flow<Int> = flow {
        for (i in 1..10) {
            delay(100)
            emit(i)
            displayThread("emitted $i")
        }
    }

    runBlocking {

        val job = launch(Dispatchers.IO) {
            flow.collect {
                displayThread("collected : $it")
            }
        }

        delay(300)
        job.cancel()
    }

}

fun <T> execute(flow: Flow<T>) {
    runBlocking {
        flow.collect {
            displayThread("collected : $it")
        }
    }
}

fun example4IntermediateOperators() {
    val flow = (1..100)
            .asFlow()
            .filter { it % 2 == 0 }
            .map { i ->
                displayThread("mapping $i")
                "processed $i in thread ${Thread.currentThread().name}"
            }
            .flowOn(Dispatchers.Default)
            .retry(2)
            .drop(30)
            .take(5)

    execute(flow)
}

private object LegacyEvents {
    private val legacyEventstore = mutableListOf<String>()

    fun addEvent(e: String) = legacyEventstore.add(e)

    fun takeEvent(): String? =
            if (legacyEventstore.isEmpty()) null
            else legacyEventstore.removeAt(0)


}

fun example5ConstantFlow() {

    val checkInterval = 500L


    val f: Flow<String> = flow {
        while (true) {
            val e = LegacyEvents.takeEvent()
            e?.let { emit(it) }
            delay(checkInterval)
        }
    }

    runBlocking {

        val job = launch(Dispatchers.Default) {
            f.collect {
                displayThread("receive event : $it")
            }
        }

        LegacyEvents.addEvent("event1")
        delay(300)
        LegacyEvents.addEvent("event2")
        LegacyEvents.addEvent("event3")
        delay(1500)
        LegacyEvents.addEvent("event4")
        delay(400)
        job.cancelAndJoin()
        displayThread("end")
    }


}

fun example6ConstantFlowWithChannel() {

    val checkInterval = 500L

    val scope= CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())

    val eventChannel=scope.produce {
        while (true) {
            val e = LegacyEvents.takeEvent()
            e?.let { send(it) }
            delay(checkInterval)
        }
    }

    val f: Flow<String> = flow {
        emitAll(eventChannel)
    }

    runBlocking {

        val job = launch(Dispatchers.Default) {
            f.collect {
                displayThread("receive event : $it")
            }
        }

        LegacyEvents.addEvent("event1")
        delay(300)
        LegacyEvents.addEvent("event2")
        LegacyEvents.addEvent("event3")
        delay(1500)
        LegacyEvents.addEvent("event4")
        delay(400)
        job.cancelAndJoin()
        displayThread("end")
    }


}