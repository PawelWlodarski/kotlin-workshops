package lodz.jug.kotlin.reactive.spring.reactor.creation

import reactor.core.publisher.Flux

fun main(args: Array<String>) {
    handleExample()
}

/**
 * An example of mapping elements with an access to a sink
 */
fun handleExample(){
    val f= Flux.just(1,-20,3,4,-5,40,6)
            .handle<String>{i,sink ->
                val elem= if(i > 0) "a".repeat(i) else "(ノಠ益ಠ)ノ彡┻━┻"
                //we have an access to a sink to we have more options for processing the value
                sink.next(elem)
            }


    f.subscribe{e -> println("prcoessing $e")}

}