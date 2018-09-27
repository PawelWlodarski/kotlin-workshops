package lodz.jug.kotlin.reactive.spring.reactor

import reactor.core.publisher.Flux
import reactor.core.publisher.SynchronousSink


fun main(args: Array<String>) {
    generateWithState()
}

private fun generateWithState() {
    val f = Flux.generate({ 0 }, { state: Int, sink: SynchronousSink<String> ->
        sink.next("processing $state")
        if (state > 15) sink.complete()
        state + 1
    })

    f.subscribe(::println)
}