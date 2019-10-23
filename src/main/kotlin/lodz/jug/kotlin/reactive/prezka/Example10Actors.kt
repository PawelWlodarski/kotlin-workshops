package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking

fun main() {


    runBlocking {
        val statefulActor=actor<Int>(Dispatchers.Default) {
            var state= 0
            for(msg in channel){
                state += msg
                println("current state is : $state")
            }
        }

        statefulActor.send(3)
        statefulActor.send(7)
        statefulActor.send(9)

        statefulActor.close()
    }
}