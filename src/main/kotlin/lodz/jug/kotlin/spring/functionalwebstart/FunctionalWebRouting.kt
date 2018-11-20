package lodz.jug.kotlin.spring.functionalwebstart

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono


//In kotlin you can create type aliases which increase readability
//Request handler is just a function from a request to Async effect of generating response - called mono
typealias RequestHandler = (ServerRequest) -> Mono<ServerResponse>

fun main(args: Array<String>) {


    //show sync version
    val helloHandler: (ServerRequest) -> Mono<ServerResponse> =
            { ServerResponse.ok().body(Mono.just("Example3"), String::class.java) }


    //For below you need a DSL instance
    //show method approach
    val dsl = RouterFunctionDsl()
    val requestPredicate: RequestPredicate = (dsl.GET("/example3").or(dsl.GET("/hello3")))


    //just a function, easy to test
    val jsonRoute : RequestHandler =  { r ->
        val response = r.bodyToMono(String::class.java)
        val result: Mono<ServerResponse> = ServerResponse.ok().body(response.map{"I received following json message \n$it"} , String::class.java)
        result
    }


    val filterFunction: (ServerRequest, HandlerFunction<ServerResponse>) -> Mono<ServerResponse> = { request, next ->
        println("start filtering")
        val response = next.handle(request)
        response
    }


    //EXPLAIN WHY INVOKE ONLY WORKS WITHIN router
    val routing=router {
        requestPredicate.invoke(helloHandler)
        //below is overloaded extension function. Not the best Idea
        accept(MediaType.APPLICATION_JSON).nest { POST("/example3json", jsonRoute) }
        "/root".nest {
            "/service".nest {
              accept(MediaType.TEXT_HTML).nest {
                  GET(""){ServerResponse.ok().body("deeplyNestedService".toMono() , String::class.java)}
              }
            }
        }
    }.filter(filterFunction)




    KotlinServer1.startReactor(routing)
}
