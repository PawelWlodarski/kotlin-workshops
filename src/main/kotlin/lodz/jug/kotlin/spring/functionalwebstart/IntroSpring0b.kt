package lodz.jug.kotlin.spring.functionalwebstart

import org.springframework.context.ApplicationContext
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.WebHandler
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono
import reactor.ipc.netty.http.server.HttpServer


fun createKotlinRouter(repo:MessageRepository) : RouterFunction<ServerResponse> {

    val healthCheck: (ServerRequest) -> Mono<ServerResponse> =  {ServerResponse.noContent().build()}

    fun findMessage(id:Int): Mono<ServerResponse> =
            repo.find(id).fold(
                    ifEmpty = { ServerResponse.notFound().build()},
                    ifSome = {message3 -> ServerResponse.ok().syncBody(message3)}
            )

    return router {
        GET("/helloKotlin"){ request ->
            ServerResponse.ok().body(Mono.just("Hello Kotlin from $request!"), String::class.java)
        }

        GET("/healthcheck",healthCheck)
        GET("/users/{id}"){
            val id = it.pathVariable("id").toInt()
            findMessage(id)
        }
    }
}


fun createContextWithDsl(): ApplicationContext  = GenericApplicationContext{
        beans {
            bean<MessageRepository>(scope = BeanDefinitionDsl.Scope.SINGLETON){LocalRepository}
            bean<WebHandler>("webHandler"){
                val repo=ref<MessageRepository>()
                RouterFunctions.toWebHandler(createKotlinRouter(repo))
            }
        }.initialize(this)
        refresh()
    }

/**
 * Its just server start
 */
fun startServerAfterDsl(ctx:ApplicationContext){
    val server = HttpServer.create(8099)
    //spring library - adapter
    val httpHandler = WebHttpHandlerBuilder.applicationContext(ctx).build()
    server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
}

fun main() {
    val ctx= createContextWithDsl()
    startServerAfterDsl(ctx)
}