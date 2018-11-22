package lodz.jug.kotlin.spring.reactor.exercises

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.test

class Mono1Answers {

    @Test
    fun `just an example`() {
        Mono
                .just("someValue")
                .test()
                .expectNextMatches { it.contentEquals("someValue") }
                .verifyComplete()
    }


    object Dao {
        private val data = mapOf(
                1 to "firstRecord",
                2 to "secondRecord"
        )

        //kotlin 1.2
        fun get(id: Int): Mono<String> {
            val record= data[id]
            return TODO("EXERCISE 1")
        }

        //kotlin1.3
//        fun get2(id:Int) = when(val record = data[id]){
//            null -> Mono.empty()
//            else -> Mono.just(record)
//        }
    }

    @Test
    fun `combining Monos`() {
        val m1 = Dao.get(1)
        val m2 = Dao.get(2)

        m1.zipWith(m2)
                .map<String> { TODO("EXERCISE 1")}
                .test()
                .expectNext("[firstRecord,secondRecord]")
                .verifyComplete()


        Dao.get(3)
                .test()
                .expectComplete()
                .verify()

    }

    @Test
    fun `Monos reduction`() {
        val m1= Mono.fromCallable{
            Thread.sleep(100)
            1
        }

        val m2 = Mono.defer { Mono.just(2) }
        val m3=Mono.create<Int> { sink ->
            sink.success(3)
        }


        val result:Int = TODO("EXERCISE 2")

        Assertions.assertEquals(result,6)
    }


    @Test
    fun `Custom Dsl`() {
            lateinit var subscriber:Exercise1JoiningSubscriber

            Flux.just("one","two","three")
                    .map { it.toUpperCase() }
                    .log()
                    .subscribe(
                            exercise1Subscriber {
                                startWith("[")
                                separateUsing(",")
                                atTheEndPut("]")
                                store{subscriber=it}
                            }
                    )


        while(!subscriber.isDisposed) Thread.sleep(100)
        Assertions.assertEquals("[ONE,TWO,THREE]",subscriber.getData())
    }




}


fun exercise1Subscriber(build :  Exercise1Dsl.() -> Unit) :Exercise1JoiningSubscriber
    = Exercise1Dsl().apply(build).create()



class Exercise1Dsl{

    lateinit var handler:(Exercise1JoiningSubscriber) -> Unit

    fun create(): Exercise1JoiningSubscriber {
        TODO("EXERCISE 3")
    }

    fun startWith(p:String){
        TODO("EXERCISE 3")
    }

    infix fun separateUsing(s:String){
        TODO("EXERCISE 3")
    }

    fun atTheEndPut(e:String){
        TODO("EXERCISE 3")
    }


    fun store(h : (Exercise1JoiningSubscriber) -> Unit) {
        handler=h
    }


}


/**
 * Base Subscriber is a high level wrapper provided by reactor
 */
class Exercise1JoiningSubscriber(private val prefix:String, private val delimiter:String,private val suffix:String)
    : BaseSubscriber<String>(){

    private var data=StringBuilder()


    fun getData():String =  TODO("EXERCISE 3")

    override fun hookOnSubscribe(subscription: Subscription?) {
        data.append(prefix)
        request(1)
    }

    override fun hookOnNext(value: String) {
        data.append("$value$delimiter")
        request(1)
    }

    override fun hookOnComplete() {
        super.hookOnComplete()
        TODO("EXERCISE 3")
    }
}