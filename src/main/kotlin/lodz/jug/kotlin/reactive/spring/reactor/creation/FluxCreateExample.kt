package lodz.jug.kotlin.reactive.spring.reactor.creation

import lodz.jug.kotlin.reactive.spring.ThreadOps
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.scheduler.Schedulers

fun main(args: Array<String>) {
    creationWithCustomSource()
}

/**
 * Explains how to combine old API with Reactor
 */
fun creationWithCustomSource(){

    //this is an adapter to a legacy source
    class SignalSource(private val sink: FluxSink<String>){
        fun signal(s:String) {
            sink.next(s)
        }

        fun complete(){
            sink.complete()
        }
    }

    lateinit var source:SignalSource

    //creates a flux which will be triggered by legacy source
    val f=Flux.create<String>{sink ->
            source= SignalSource(sink)
    }


    f.log()
     .publishOn(Schedulers.parallel())
     .subscribe{e->
        println("emitted element in ${ThreadOps.threadName()} $e")
    }


    source.signal("customSignal1")
    source.signal("customSignal2")
    source.complete()

}