package lodz.jug.kotlin.spring.functionalwebstart.answers

import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.test.test

class Part1AnswerTests {


    //problem - https://youtrack.jetbrains.com/issue/KT-5464
    //    https://jira.spring.io/browse/SPR-15692
    //    val testClient = WebTestClient.bindToApplicationContext(SpringPart1Answers.ctx).build()
    //    @Test
    //    fun `simple get`() {
    //        testClient.get()
    //                .uri("/exercise1get")
    //                .exchange()
    //                .expectStatus().isOk
    //                .expectBody().returnResult().apply { assertEquals("aa",responseBody) }
    //    }


    private val client = WebClient.create("http://localhost:9050")

    @Test
    fun `simple get`(){
        client.get()
                .uri("/exercise1get")
                .retrieve()
                .bodyToMono<String>()
                .test()
                .expectNextMatches{it.contentEquals("Hello exercise 1")}
                .verifyComplete()

    }

}