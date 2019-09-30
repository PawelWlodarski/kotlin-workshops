package lodz.jug.kotlin.reactive.croutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {

    example1ObservingButton()


//    example1producerConsumer()
//    example2ClosingChannel()
//    example3BuildingProducer()

}

/** given **/
object SomeView {

    private val communication = Channel<UIEvent>()

    object SomeButton{
        var onClick:() -> Unit = {}

        fun click(){
            onClick()
        }
    }

    object ExitButton{

    }
}

private enum class UIEvent{
    ButtonClicked
}


private fun example1ObservingButton(){

    val observerChannel= Channel<UIEvent>()

    displayThread("button clicked")

    ExampleContexts.uiScope.launch {
        displayThread("onClick")
        observerChannel.send(UIEvent.ButtonClicked)
    }


    ExampleContexts.ioScope.launch {
        val event=observerChannel.receive()
        displayThread("received event :  $event")
    }



}


private fun example3BuildingProducer() {
    fun CoroutineScope.produceChannel(): ReceiveChannel<Int> =
            produce {
                for (i in 1..5) send(i)
            }


    runBlocking {
        val producer = produceChannel()

        producer.consumeEach(::println)
        println("after consumption")
    }
}


private fun example2ClosingChannel() {
    runBlocking {
        val channel = Channel<Int>()
        launch {
            for (i in 1..5) channel.send(i)
            channel.close()
        }



        for (msg in channel) println(msg)
        println("channel closed")
    }
}


fun example1producerConsumer() = runBlocking {
    val channel = Channel<Int>()
    launch {
        for(x in 1..10) channel.send(x * x)
    }


    repeat(10){
        val v=channel.receive()
        println("received from channel : $v")
    }

    println("done")
}