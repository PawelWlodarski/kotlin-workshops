package lodz.jug.kotlin.spring.functionalwebstart

import arrow.core.Option
import arrow.core.toOption
import org.springframework.beans.factory.config.BeanDefinitionCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.server.WebHandler
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono
import reactor.ipc.netty.http.server.HttpServer
import java.util.function.Supplier


/**
 * 1. Dependencies
 *    Simple repo symulation
 */
interface MessageRepository{
    fun find(id:Int) : Option<String>
}

object LocalRepository : MessageRepository{

    private val messages= mapOf(
            1 to "message1",
            2 to "message2",
            3 to "message3"
    )


    override fun find(id: Int): Option<String>  = messages[id].toOption()
}


/**
 * 2. Routing
 *  a) In Kotlin you need to explicitly say that given function is Functional Interface HandlerFunctional
 *  b) So called "Functional API" uses WebFlux so you will see very often Mono or Flux which are async abstractions
 *  c) HandlerFunction is a function (Request) ->  Mono<Response> so you can retrieve all info about parameters or headers
 *  from the request
 *  d) to build routing you need to use static methods "route" and GET,POST etc
 *      To add more routes you just using chain calls "andRoute" or "andNest"
 */
fun createJavaRouter(repo:MessageRepository) : RouterFunction<ServerResponse> {
    val healthCheck: HandlerFunction<ServerResponse> =  HandlerFunction{ _ -> ServerResponse.noContent().build()}

    fun findMessage(id:Int): Mono<ServerResponse> =
        repo.find(id).fold(
                ifEmpty = {ServerResponse.notFound().build()},
                ifSome = {message3 -> ServerResponse.ok().syncBody(message3)}
        )

    fun messageHandler() = HandlerFunction {r ->
        val id=r.pathVariable("id").toInt()
        findMessage(id)
    }


    return route(GET("/helloJava"),
            HandlerFunction{request -> ServerResponse.ok().body(Mono.just("Hello World from $request!"), String::class.java)}
    )
            .andRoute(GET("/healthcheck"),healthCheck)
            .andRoute(GET("/users/{id}"),messageHandler())

}

enum class CustomScope { SINGLETON, PROTOTYPE }


/**
 *  3. Context Initialization
 *      a) BeanDefinitionCustomizer can be passed during bean registration to set scope and other bean properties
 *      b) If we want to create instance by ourselves then we need to create Supplier
 *      c) We need to call "refresh" only once to activate bean factory
 *      d) To use webflux we need to convert our routing to WebHandler - there is ready to use static Method RouterFunctions.toWebHandler
 */
fun createContext():ApplicationContext{
    val ctx= GenericApplicationContext()

    //Repository bean
    val repositoryCustomizer = BeanDefinitionCustomizer { bd ->
        bd.scope = CustomScope.SINGLETON.name.toLowerCase()
    }
    ctx.registerBean(MessageRepository::class.java, Supplier<MessageRepository> { LocalRepository } ,repositoryCustomizer)
    ctx.refresh()

    //Routing bean
    val repo=ctx.getBean(MessageRepository::class.java)
    val router: RouterFunction<ServerResponse> =createJavaRouter(repo)
    val webHandler: WebHandler = RouterFunctions.toWebHandler(router)
    ctx.registerBean("webHandler",WebHandler::class.java ,Supplier{webHandler})
    return ctx
}


/**
 * Its just server start
 */
fun startServer(ctx:ApplicationContext){
    val server = HttpServer.create(8060)
    //spring library - adapter
    val httpHandler = WebHttpHandlerBuilder.applicationContext(ctx).build()
    server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
}

fun main() {
    val ctx= createContext()
    startServer(ctx)
}