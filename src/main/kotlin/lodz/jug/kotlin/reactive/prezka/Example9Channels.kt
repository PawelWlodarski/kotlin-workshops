package lodz.jug.kotlin.reactive.prezka

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    data class Message(val payload:String)

    val channel = Channel<Message>()


    GlobalScope.launch {
        (1 .. 10).forEach{
            channel.send(Message("payload$it"))
            delay(100)
        }
        channel.close()  //this is dangerous
    }


    launch {
        for(m in channel) println("received $m")
    }


    Unit
}

