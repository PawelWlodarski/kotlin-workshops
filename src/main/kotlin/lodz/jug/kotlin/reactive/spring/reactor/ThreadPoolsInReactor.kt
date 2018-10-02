package lodz.jug.kotlin.reactive.spring.reactor

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import lodz.jug.kotlin.reactive.spring.FluxExtensions.flatMapEx
import lodz.jug.kotlin.reactive.spring.reactor.subscribers.BatchingSubscriber


fun main(args: Array<String>) {
//    singleThreadFlux()
//    singleThreadWithErrorHandler()
//    differentScheduler()
//    customSubscriber()
    flatMappedThreads()

}


private fun singleThreadFlux(){
    Flux
            .just("one","two","three","four","five")
            .log()
            .map { it.toUpperCase() }
            .subscribe()
}

private fun singleThreadWithErrorHandler(){
    Flux
            .range(-5, 10)
            .log()
            .map { 100 / it }
            .subscribe(::println){error ->
                println("ERROR!!!")
                println(error.message)
            }
}

private fun differentScheduler(){
    Flux
            .just("one","two","three","four","five")
            .log()
            .map { it.toUpperCase() }
            .subscribeOn(Schedulers.parallel())
            .subscribe()
//            .subscribe(::println)

    Thread.sleep(100)
}

private fun customSubscriber(){
    Flux
            .just("one","two","three","four","five")
            .log()
            .map { it.toUpperCase() }
            .subscribeOn(Schedulers.parallel())
            .subscribe(BatchingSubscriber(2))

    Thread.sleep(100)
}



private fun flatMappedThreads() {
    Flux
            .just("one", "two", "three", "four")
            .log()
            .flatMapEx(2) { Mono.just(it.toUpperCase()).subscribeOn(Schedulers.parallel()) }
            .subscribe { println("consumed : $it") }


    Thread.sleep(100)
}




