package lodz.jug.kotlin.spring.functionalwebstart

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import lodz.jug.kotlin.Displayer
import reactor.core.publisher.Mono
import java.time.Duration
import reactor.core.publisher.toMono


/**
 * This file explains Async constructions you came across when using WebFlux
 *
 * exercises - IntroSpring3Exercises
 */
typealias SyncFunction = (IntroSpring3.Request) -> IntroSpring3.Response
typealias AsyncServerFunction3 = (IntroSpring3.Request) -> Mono<IntroSpring3.Response>

fun <T> Mono<T>.defer() = Mono.defer { this }

fun main() {
    Displayer.header("spring preparation - REACTIVE")

    val requestQueue = (1..5).map { IntroSpring3.Request("/getUSer", it) }

    blockingExample(requestQueue)
//    reactiveExample(requestQueue)

    //FLAT_MAP example
//    println(flatMapExample(1).block())
//    println(flatMapExample(5).block())


}


private fun blockingExample(requestQueue: List<IntroSpring3.Request>) {
    val syncHandler1: SyncFunction = { r ->
        val user = LocalDao.find(UserId(r.id))
        IntroSpring3.Response("Found user $user")
    }


    val timeout = Mono
            .delay(Duration.ofSeconds(2))
            .subscribe { throw RuntimeException("timeout") }


    requestQueue.map(syncHandler1).forEach { response ->
        println("received response : $response")
        timeout.dispose()
    }
}

fun reactiveExample(requestQueue: List<IntroSpring3.Request>) {
    val asyncHandler: AsyncServerFunction3 = { r ->
        Mono.defer { LocalDao.find(UserId(r.id)).toMono() }  //toMono -> Kotlin extension
                .map { user -> IntroSpring3.Response("Found user $user") }
        //or
//        LocalDao.find(UserId(r.id)).toMono().defer()
    }

    val timeout = Mono
            .delay(Duration.ofSeconds(2))
            .subscribe { throw RuntimeException("timeout") }


    requestQueue.map(asyncHandler).forEach { responseMono: Mono<IntroSpring3.Response> ->
        responseMono
                .subscribe { println("received response : $it") }

        timeout.dispose()
    }

}


fun flatMapExample(id:Int): Mono<UserRecord> {
    val r = IntroSpring3.Request("/getUSer", id)

   return  Mono.defer { LocalDao.find(UserId(r.id)).toMono() }.flatMap {
        when (it) {
            is Some -> Mono.just(it.t)
            is None ->  Mono.empty()
        }
    }

}

inline class UserId(val id: Int)
data class UserRecord(val id: UserId, val name: String, val age: Int)
interface Dao {
    fun find(id: UserId): Option<UserRecord>
}

object LocalDao : Dao {

    private val data = mapOf<UserId, UserRecord>(
            UserId(1) to UserRecord(UserId(1), "Romek", 30)
    )

    override fun find(id: UserId): Option<UserRecord> {
        Thread.sleep(1000)
        return Option.fromNullable(data[id])
    }
}

object IntroSpring3 {
    data class Request(val path: String, val id: Int)
    data class Response(val content: String)
}