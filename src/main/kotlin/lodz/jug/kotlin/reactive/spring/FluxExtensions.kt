package lodz.jug.kotlin.reactive.spring

import org.reactivestreams.Publisher
import reactor.core.publisher.Flux

object FluxExtensions {
    fun <A,B> Flux<A>.flatMapEx(concurrency:Int, mapper : (A) -> Publisher<B>) : Flux<B> =
            this.flatMap(mapper, concurrency)

}