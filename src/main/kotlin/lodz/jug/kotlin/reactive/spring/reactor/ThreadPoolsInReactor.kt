package lodz.jug.kotlin.reactive.spring.reactor

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import lodz.jug.kotlin.reactive.spring.FluxExtensions.flatMapEx
import lodz.jug.kotlin.reactive.spring.ThreadOps
import lodz.jug.kotlin.reactive.spring.reactor.subscribers.BatchingSubscriber


fun main(args: Array<String>) {
//    singleThreadFlux()
//    singleThreadWithErrorHandler()
//    differentScheduler()
//    customSubscriber()
//    flatMappedThreads()
    runOnWhichThread()
//    canNotBlockInReactive()
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


private fun runOnWhichThread() {
    val s = Schedulers.newParallel("4Pancerni",4)

    val flux = Flux
            .range(1, 3)
            .log()
            .map { i ->
                ThreadOps.printThreadName()
                10 + i }
            .publishOn(s)
            .map { i ->
                ThreadOps.printThreadName()
                "value $i"
            }

    val t=Thread { flux.subscribe({ println(it) }) }
    t.start()
    t.join()

}

private fun canNotBlockInReactive()  {
    println("testing blocking in parallel")
    val blocksInParallel=Flux.just("one","two","three")
            .publishOn(Schedulers.parallel())
            .subscribeOn(Schedulers.parallel())
            .flatMap{input ->
                val m=Mono.just("$input in mono")
                m.block()
                m
            }

//    blocksInParallel.blockFirst()
    blocksInParallel.doOnError {
        println("ERROR OCCURED")
        it.printStackTrace()
    }
    blocksInParallel.subscribe{println("this should not work")}

    Thread.sleep(100)
}





