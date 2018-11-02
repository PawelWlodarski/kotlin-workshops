package lodz.jug.kotlin.spring.functionalwebstart


import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just
import reactor.ipc.netty.http.server.HttpServer


//BEAN interface
interface RepositoryExample2 {
    fun find(id: Int): Mono<String>
}

//BEAN implementation
object InMemoryRepo : RepositoryExample2 {
    override fun find(id: Int): Mono<String> = if (id == 1) Mono.just("SUCCESS") else Mono.empty()
}

object FunctionalExample2 {

    //Kotlin-reflect needed.  Repository Injected
    private fun routing(repo: RepositoryExample2): RouterFunction<ServerResponse> =
        router {
            GET("/hello") { _ ->
                ok().body(just("Example 2"), String::class.java)
            }
            //next mapping in new line
            GET("/find/{id}") { request ->
                val response = Mono.justOrEmpty(request.pathVariable("id"))
                        .map(Integer::valueOf)
                        .flatMap(repo::find)
                ok().body(response, String::class.java)
            }

        }


    fun example2Beans(): BeanDefinitionDsl =
        beans {
            bean<InMemoryRepo>()
            bean("webHandler") {
                val repo= ref<InMemoryRepo>()
                RouterFunctions.toWebHandler(routing(repo))
            }
        }



}

object KotlinServer2 {

    fun startReactor(beansDefinition : BeanDefinitionDsl, host: String = "localhost", port: Int = 9050) {
        val ctx = GenericApplicationContext {
            beansDefinition.initialize(this)
            refresh()
        }

        val server = HttpServer.create(port)
        val httpHandler = WebHttpHandlerBuilder.applicationContext(ctx).build()
        server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
    }
}


fun main(args: Array<String>) {
    KotlinServer2.startReactor(FunctionalExample2.example2Beans())
}