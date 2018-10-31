package lodz.jug.kotlin.spring.functionalwebstart


import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono.just
import reactor.ipc.netty.http.server.HttpServer


fun helloRouterFunction(): RouterFunction<ServerResponse> {
    return router {
        GET("/hello") { _ ->
            ok().body(just("Hello World!"), String::class.java)
        }
    }
}


object KotlinServer {

    fun startReactor(router : RouterFunction<ServerResponse>, host:String="localhost", port:Int=9050) {
        val ctx = GenericApplicationContext{
            beans {
                bean("webHandler"){
                    RouterFunctions.toWebHandler(router)
                }
            }.initialize(this)
            refresh()
        }

        val server = HttpServer.create(port)
        val httpHandler = WebHttpHandlerBuilder.applicationContext(ctx).build()
        server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
    }
}

fun main(args: Array<String>) {

        KotlinServer.startReactor(helloRouterFunction())
}