package lodz.jug.kotlin.spring.functionalwebstart.answers

import org.amshove.kluent.shouldEqual
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.test
import java.time.Duration
import java.util.*

class IntroSpring3Answers {

    @Test
    fun shouldReadAsynchronouslyFromDB() {
        val r = (1..5)
                .map { ProductId(it) }
                .map(Intro3AnswersLibrary.InMemoryDatabase::get)


        val reducedToFlux = Flux.concat<Intro3AnswersLibrary.ProductRecord>(*r.toTypedArray())


        //variant 1
        reducedToFlux.test()
                .expectNext(Intro3AnswersLibrary.ProductRecord(ProductId(1), "PC", 1000.0))
                .expectNext(Intro3AnswersLibrary.ProductRecord(ProductId(2), "Book", 30.0))
                .verifyComplete()


        //variant2
        reducedToFlux
                .collectList()
                .test()
                .expectNext(
                        listOf(
                                Intro3AnswersLibrary.ProductRecord(ProductId(1), "PC", 1000.0),
                                Intro3AnswersLibrary.ProductRecord(ProductId(2), "Book", 30.0)
                        )
                )
                .verifyComplete()

    }


    //PART2
    @Test
    fun shouldHandleRequestWithExistingId() {
        val r1 = Intro3AnswersLibrary.Handler.Request("1")
        val r2 = Intro3AnswersLibrary.Handler.Request("5")
        val r3 = Intro3AnswersLibrary.Handler.Request("zz")

        Intro3AnswersLibrary.Handler.handle(r1).test()
                .expectNext(Intro3AnswersLibrary.Handler.Response("received PC with price 1000.0"))
                .verifyComplete()

        Intro3AnswersLibrary.Handler.handle(r2).test().verifyComplete()

        Intro3AnswersLibrary.Handler.handle(r3).test().verifyErrorMessage("zz is not a number")
    }



    //PART3
    @Test
    fun shouldHandleRequestWithExistingIdAndAddToEventStore() {
        val r1 = Intro3AnswersLibrary.Handler.Request("1")
        val r2 = Intro3AnswersLibrary.Handler.Request("5")
        val r3 = Intro3AnswersLibrary.Handler.Request("zz")

        Intro3AnswersLibrary.Handler.handleEvent(r1).test()
                .expectNext(Intro3AnswersLibrary.Handler.Response("received PC with price 1000.0"))
                .verifyComplete()

        Intro3AnswersLibrary.Handler.handleEvent(r2).test().verifyComplete()

        Intro3AnswersLibrary.Handler.handleEvent(r3).test().verifyErrorMessage("zz is not a number")


        Intro3AnswersLibrary.EventStore.events() shouldEqual listOf("Event for id=1,name=PC") //this matcher is from kluent
    }

}


inline class ProductId(val id: Int)

object Intro3AnswersLibrary {


    data class ProductRecord(val id: ProductId, val name: String, val price: Double)
    interface Database {
        fun get(id: ProductId): Mono<ProductRecord>
    }

    object InMemoryDatabase : Database {

        private val data = Data

        override fun get(id: ProductId): Mono<ProductRecord> = Mono.defer { Mono.justOrEmpty(data.get(id)) }

    }

    private object Data {

        private val p1 = ProductRecord(ProductId(1), "PC", 1000.0)
        private val p2 = ProductRecord(ProductId(2), "Book", 30.0)

        private val data = mapOf(
                1 to p1,
                2 to p2
        )

        fun get(pid: ProductId): ProductRecord? {
            Thread.sleep(500)
            return data[pid.id]
        }
    }


    //PART 2
    object EventStore {
        private var events: Queue<String> = LinkedList()

        private fun createEvent(record:ProductRecord):String = "Event for id=${record.id.id},name=${record.name}"

        //should we save to db in map function?
        fun addEvent(e: ProductRecord): Mono<Boolean> = Mono.just(createEvent(e)).map(events::offer).delayElement(Duration.ofSeconds(1))

        fun events(): List<String> = events.toList()
    }

    object Handler {
        data class Request(val id: String)
        data class Response(val content: String)

        fun handle(r: Request): Mono<Response> =
                validate(r)
                        .flatMap { InMemoryDatabase.get(it) }   //dependency injection in the next module
                        .map(generateResponse)


        //PART 3
        fun handleEvent(r:Request): Mono<Response> =
            validate(r)
                    .flatMap { InMemoryDatabase.get(it) }   //dependency injection in the next module
                    .doOnNext{EventStore.addEvent(it).block()}
                    .map(generateResponse)


        //TODO: use this one in exercise
        private val generateResponse: (ProductRecord) -> Response = { Response("received ${it.name} with price ${it.price}") }

        private fun validate(r: Request): Mono<ProductId> = try {
            Mono.just(ProductId(r.id.toInt()))
        } catch (e: NumberFormatException) {
            Mono.error(RuntimeException("${r.id} is not a number"))
        }

    }

}