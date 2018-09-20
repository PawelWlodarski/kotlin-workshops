package lodz.jug.kotlin.reactive.spring.reactor

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import lodz.jug.kotlin.reactive.spring.FluxExtensions.flatMapEx



fun main(args: Array<String>) {
//    singleThreadFlux()
    singleThreadWithErrorHandler()
//    flatMappedThreads()

}


private fun singleThreadFlux(){
    Flux
            .just("one","two","three")
            .log()
            .map { it.toUpperCase() }
            .subscribe()
//            .subscribe(::println)
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

private fun flatMappedThreads() {
    Flux
            .just("one", "two", "three", "four")
            .log()
            .flatMapEx(2) { Mono.just(it.toUpperCase()).subscribeOn(Schedulers.parallel()) }
            .subscribe { println("consumed : $it") }


    Thread.sleep(100)
}




