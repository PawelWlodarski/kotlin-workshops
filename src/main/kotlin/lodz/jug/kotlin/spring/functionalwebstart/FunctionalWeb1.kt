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
    //This one uses official kotlin DSL for building Routerfunction and Handlerfunctions
    //an example of building such dsl can be found in IntroSpring1.kt
    return router {
        //for explanation how GET is build take a look at IntroSpring2
        GET("/hello") { _ ->
            //and below is HandlerFunction
            // Handler function is explained in IntroSpring2
            //and Reactor api in IntroSpring2 and MonoDemo
            ok().body(just("Hello World!"), String::class.java)
        }
    }
}


object KotlinServer1 {

    fun startReactor(router : RouterFunction<ServerResponse>, host:String="localhost", port:Int=9050) {
        val ctx = GenericApplicationContext{
            // Explain kotlin dsl
            beans {
                bean("webHandler"){
                    RouterFunctions.toWebHandler(router) //<- change our routing intpo spring component
                }
            }.initialize(this)
            refresh()
        }

        //reactory library
        val server = HttpServer.create(port)
        //spring library - adapter
        val httpHandler = WebHttpHandlerBuilder.applicationContext(ctx).build()
        server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
    }
}

fun main(args: Array<String>) {

        KotlinServer1.startReactor(helloRouterFunction())
}