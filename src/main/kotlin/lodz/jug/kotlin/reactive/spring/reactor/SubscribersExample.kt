package lodz.jug.kotlin.reactive.spring.reactor

import lodz.jug.kotlin.reactive.spring.reactor.subscribers.BatchingSubscriber
import lodz.jug.kotlin.reactive.spring.reactor.subscribers.SimpleSubscriber
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers

fun main(args: Array<String>) {
    simpleSubscriber()
//    batchingSubscriber()
}


fun simpleSubscriber() {
    Flux
            .just("one","two","three","four","five")
            .log()
            .map { it.toUpperCase() }
            .subscribeOn(Schedulers.parallel())
            .subscribe(SimpleSubscriber<String>())

    Thread.sleep(100)
}


private fun batchingSubscriber(){
    Flux
            .just("one","two","three","four","five")
            .log()
            .map { it.toUpperCase() }
            .subscribeOn(Schedulers.parallel())
            .subscribe(BatchingSubscriber(2))

    Thread.sleep(100)
}