package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() {
    runBlocking {
        val producer: Flow<Int> = flow<Int> {
            repeat(100000) {
                delay(1)
                emit(it)
            }
        }


        launch {
            producer.conflate()
                    .collect { value ->
                        println("collecting $value")
                        delay(100)
                    }
        }
    }
}