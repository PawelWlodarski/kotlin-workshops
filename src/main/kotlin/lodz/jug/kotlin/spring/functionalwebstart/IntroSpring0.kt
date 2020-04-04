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
 * THIS IS AN EXAMPLE OF USING JAVA API IN KOTLIN.
 * KOTLIN API IS IN IntroSpring0b
 */

fun main() {
    val ctx= createContext()
    startServer(ctx)
}

/**
 *  Context Initializatio
 *      1) Create GenericApplicationContext which allow to instantly register beans
 *      2) Optionally create bean customizers
 *      3) Register your dependency with Supplier
 *      4) Refresh
 *      5) CreateRouting ***
 *      6)Convert routing to WebFlux WebHandler
 *
 */
fun createContext():ApplicationContext{
    //https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/GenericApplicationContext.html
    val ctx= GenericApplicationContext()

    //Repository bean :https://www.logicbig.com/tutorials/spring-framework/spring-core/spring-5-bean-registration.html

    //to understand syntaxt take a look at usingJavaFunctionalInterface in "scratches"
    val singletonScopeCustomizer = BeanDefinitionCustomizer { bd ->
        bd.scope = CustomScope.SINGLETON.name.toLowerCase()
    }

    //here you can also see Supplier declaration syntax
    ctx.registerBean(MessageRepository::class.java, Supplier<MessageRepository> { LocalRepository } ,singletonScopeCustomizer)
    ctx.refresh()

    //Routing bean
    val repo=ctx.getBean(MessageRepository::class.java)
    val router: RouterFunction<ServerResponse> =createJavaRouter(repo)
    val webHandler: WebHandler = RouterFunctions.toWebHandler(router)
    ctx.registerBean("webHandler",WebHandler::class.java ,Supplier{webHandler})
    return ctx
}


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
 *  a) Declare HandlerFunctions - Functions : Request -> ServerResponse
 *   ** for more complex handler extract logic.
 *
 *
 *  b) So called "Functional API" uses WebFlux so you will see very often Mono or Flux which are async abstractions
 *  c) HandlerFunction is a function (Request) ->  Mono<Response> so you can retrieve all info about parameters or headers
 *  from the request
 *  d) to build routing you need to use static methods "route" and GET,POST etc
 *      To add more routes you just using chain calls "andRoute" or "andNest"
 */
fun createJavaRouter(repo:MessageRepository) : RouterFunction<ServerResponse> {

    //using java functional Interface
    val healthCheck: HandlerFunction<ServerResponse> =  HandlerFunction{ _ -> ServerResponse.noContent().build()}

    //find(id) returns option
    //syncBody returns finished Mono <-- reactor
    fun findMessage(id:Int): Mono<ServerResponse> =
        repo.find(id).fold(
                ifEmpty = {ServerResponse.notFound().build()},
                ifSome = {message3 -> ServerResponse.ok().syncBody(message3)}
        )

    fun messageHandler() = HandlerFunction {r ->
        val id=r.pathVariable("id").toInt()
        findMessage(id)
    }

    //below and example of "ad hoc" route definition, and usage of previously defined handler functions
    //This is Java native API, intuitive but cumbersome
    return route(GET("/helloKotlin"),
            HandlerFunction{request -> ServerResponse.ok().body(Mono.just("Hello World from $request!"), String::class.java)}
    )
            .andRoute(GET("/healthcheck"),healthCheck)
            .andRoute(GET("/users/{id}"),messageHandler())

}

enum class CustomScope { SINGLETON, PROTOTYPE }





/**
 * Its just server start
 */
fun startServer(ctx:ApplicationContext){
    val server = HttpServer.create(8060)
    //spring library - adapter
    val httpHandler = WebHttpHandlerBuilder.applicationContext(ctx).build()
    server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
}

