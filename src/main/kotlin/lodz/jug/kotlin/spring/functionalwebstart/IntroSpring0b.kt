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


/**
 * THIS IS AN EXAMPLE OF USING JAVA API IN KOTLIN.
 * KOTLIN API IS IN IntroSpring0a
 *
 *
 * KOTLIN SYNTAX FOR ROUTING IS EXPLAINED IN IntroSpring1 File
 */
fun createKotlinRouter(repo:MessageRepository) : RouterFunction<ServerResponse> {

    //in kotlin API layer you don't need to specify HandlerFunction type, just write standard function
    val healthCheck: (ServerRequest) -> Mono<ServerResponse> =  {ServerResponse.noContent().build()}

    fun findMessage(id:Int): Mono<ServerResponse> =
            repo.find(id).fold(
                    ifEmpty = { ServerResponse.notFound().build()},
                    ifSome = {message3 -> ServerResponse.ok().syncBody(message3)}
            )

    //This is Kotlin native API, Short but uses some specific kotlin features
    //no method chaining
    //looks like standard config
    return router {
        //ad hoc declaration
        GET("/helloKotlin"){ request ->
            ServerResponse.ok().body(Mono.just("Hello Kotlin from $request!"), String::class.java)
        }

        //previously defined handler function
        GET("/healthcheck",healthCheck)

        //it kotlin syntax
        GET("/users/{id}"){
            val id = it.pathVariable("id").toInt()
            findMessage(id)
        }
    }
}




//GenericApplicationContext uses "lambda with receiver"
//beans is an independent dsl and can be moved outside GenericApplication context definition
fun createContextWithDsl(): ApplicationContext  = GenericApplicationContext{
        beans {
            bean<MessageRepository>(scope = BeanDefinitionDsl.Scope.SINGLETON){LocalRepository}
            bean<WebHandler>("webHandler"){
                //inline function with reified generics
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