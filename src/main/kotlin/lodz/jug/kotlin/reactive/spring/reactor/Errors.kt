package lodz.jug.kotlin.reactive.spring.reactor

import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

fun main(args: Array<String>) {
//    throwingExceptionFromSuccess()
//    throwingExceptionFromSuccessInSeparatePool()

    MonoErrorSingleton()
}



private fun throwingExceptionFromSuccess() {
    //MonoPeekTerminal onComplete signaling error
    //here we have just a function
    val m = Mono.just(7).doOnSuccess { arg ->
        ThreadOps.printThreadName()
        if (arg < 10) throw IllegalStateException("an error")
    }

    Thread.sleep(1000)
    //no call to doOnError
    m.doOnError { e ->
        println("an error occured")
        e.printStackTrace()
    }

    //BlockingSingleSubscriber.onError
    println("result ${m.block()}")
}

private fun throwingExceptionFromSuccessInSeparatePool() {
    val m = Mono.just(7)
            .publishOn(Schedulers.parallel())
            .doOnSuccess { arg ->
        ThreadOps.printThreadName()
        if (arg < 10) throw IllegalStateException("my exception in on Success")
    }

    Thread.sleep(1000)
    m.doOnError { e ->
        println("an error occured")
        e.printStackTrace()
    }
//    println("result ${m.block()}")
}


private fun MonoErrorSingleton() {
    val m = Mono.just(7)
            .publishOn(Schedulers.parallel())
            .flatMap { arg ->
                ThreadOps.printThreadName()
                if (arg < 10)
                    Mono.error(IllegalStateException("exception in mono error"))
                else
                    Mono.just(arg)
            }

    //still not called
    m.doOnError { e ->
        println("an error occured")
        e.printStackTrace()
    }

    m.subscribe{e -> println("subscription $e")}

    Thread.sleep(1000)

//    println("result ${m.block()}")
}