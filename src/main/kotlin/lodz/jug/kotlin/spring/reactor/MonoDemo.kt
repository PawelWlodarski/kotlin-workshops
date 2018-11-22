package lodz.jug.kotlin.spring.reactor

import lodz.jug.kotlin.Displayer
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

fun main(args: Array<String>) {
    Displayer.header("Understand Reactor Mono")

    //show debug logs
    // theSimplestExample()  //1

//    differentThreadExample() //2

    //blockAndDisposable() //3
    //threadPools()   //4
    multipleMonos() //5
}

//show debug logs
private fun theSimplestExample() {
    Mono
            .just("result")
            .log()
            .subscribe()
}

private fun differentThreadExample() {
    Mono.just("result2")
            .log()
            .map(String::toUpperCase)
            .log()
            .subscribeOn(Schedulers.parallel())
            .subscribe()

    //why sleep is needed here?
    Thread.sleep(100)
}

private fun blockAndDisposable() {
    val m: Mono<String> = Mono.just("block and disposable")
            .log()
            .map(String::toUpperCase)
            .subscribeOn(Schedulers.parallel())

    val subscription: Disposable = m.subscribe()  //comment this one and check what will happen
//    subscription.dispose() //check this one
    val result=m.block()
    println("result after blocking $result")


}


private fun threadPools() {

    val s1 = Schedulers.newParallel("firstPool",4)
    val s2 = Schedulers.newParallel("secondPool",3)

    val m: Mono<String> = Mono.just("block and disposable")
            .log()
            .map(String::toUpperCase)
            .map {
                println("mapping in ${Thread.currentThread().name}")
                it + "mapped"
            }
            .subscribeOn(s1)

    m.publishOn(s2)
            .subscribe{println("In thread ${Thread.currentThread().name}")}


    m.subscribe{println("second subscribe in  ${Thread.currentThread().name}")}
    m.publishOn(s2)
            .subscribe{println("third subscribe in  ${Thread.currentThread().name}")}


}

private fun multipleMonos(){
    fun blockingOperation(r:String): String {
        Thread.sleep(500)
        return r
    }

    val m1= Mono.fromSupplier<String>{ blockingOperation("operation1")  }
    val m2= Mono.fromSupplier<String>{ blockingOperation("operation2")  }

    val subscription=m1.zipWith(m2)
            .subscribeOn(Schedulers.parallel())
            .subscribe {
        println("received tuple $it")
    }

    while(!subscription.isDisposed){
        println("waiting for zipped result")
        Thread.sleep(100)
    }


}





