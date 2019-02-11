package lodz.jug.kotlin.spring.functionalwebstart

import lodz.jug.kotlin.Displayer
import lodz.jug.kotlin.spring.functionalwebstart.IntroSpring2.GET
import lodz.jug.kotlin.spring.functionalwebstart.IntroSpring2.Request
import lodz.jug.kotlin.spring.functionalwebstart.IntroSpring2.Response
import lodz.jug.kotlin.spring.functionalwebstart.IntroSpring2.buildRoute2
import lodz.jug.kotlin.spring.functionalwebstart.IntroSpring2.router2

typealias ServerFunction2 = (IntroSpring2.Request) -> IntroSpring2.Response

fun main() {
    Displayer.header("spring preparation - CREATING ROUTES")

    /**
     * every route is a high order function (request) -> (request) -> response
     * It receives request and returns another function which can handle given request
     *
     * so if we imagine that ServerHandler is something which conversts request to response
     * then signature will be
     * ServerHandler = (request) -> response
     * Routing = Map<request, ServerHandler>
     *
     *
     *
     */
    val route1 =
            buildRoute2(GET("/hello")) { req ->
                Response("response on path ${req.path}")
            }

    val route2 =
            buildRoute2(GET("/admin")) { req ->
                Response("response on path ${req.path}")
            }

    val routeOr =
            buildRoute2(GET("/admin") or GET("/hello")) { req ->
                Response("response on path ${req.path}")
            }


    val r = Request(path = "/hello", method = IntroSpring2.METHOD.GET)
    Displayer.section("route1 when calling '/hello'", route1(r).invoke(r)) //first find handler, then call handler

    val handler2: ServerFunction2 = route2(r)
    Displayer.section("route2 when calling '/admin'", handler2(r))

    val handlerOr: ServerFunction2 = routeOr(r)
    Displayer.section("routeOr when calling '/admin or /hello'", handlerOr(r))


    /**
     * 1) notice that we have two overloaded get methods here
     * ** one for creating routing from string path
     * ** another one operates on Directives so we can easily compine different paths
     */
    Displayer.title("Routing dsl example 2")
    val routes=router2 {
        get("/hello") { r ->
            Response("response for $r")
        }

        //Extract "/index".or("/") outside routes function and see what happens
        get("/index".or("/")){
            Response("response for $it")
        }
    }

    Displayer.title("displaying responses for request on /hello")
    routes.map { route -> route(r)(r) }.forEach(::println)

    Displayer.title("displaying responses for request on /index")
    val r2 = Request(path = "/index", method = IntroSpring2.METHOD.GET)
    routes.map { route -> route(r2)(r2).toString() }.forEach(Displayer::section)  // TRY without toString !!!



}


class RouterDsl2 {
    private var routes = emptyList<(Request) -> ServerFunction2>()

    /**
     * This is very important part!
     * or is a LOCAL extension method wo String will have access to it only when called withing implicit 'this' scope
     */
    fun String.or(other: String): IntroSpring2.RoutingPredicate = IntroSpring2.SinglePredicate(this).or(IntroSpring2.SinglePredicate(other))

    fun get(path: String, handler: ServerFunction2) {
        routes = routes + buildRoute2(GET(path), handler)
    }

    fun get(path: IntroSpring2.RoutingPredicate, handler: ServerFunction2) {
        routes += buildRoute2(path, handler)
    }

    fun routes() = routes
}


object IntroSpring2 {
    enum class METHOD { GET, SET }
    data class Request(val path: String, val method: METHOD)
    data class Response(val content: String)


    interface RoutingPredicate {
        fun test(candidate: String): Boolean
    }

    class SinglePredicate(private val path: String) : RoutingPredicate {
        override fun test(candidate: String) = this.path == candidate

        //what is infix?
        infix fun or(other: RoutingPredicate) = object : RoutingPredicate { //This is how you are creating anonymous objects
            override fun test(candidate: String): Boolean = this@SinglePredicate.test(candidate) || other.test(candidate)
        }
    }

    fun GET(pattern: String) = SinglePredicate(pattern)

    fun buildRoute2(p: IntroSpring2.RoutingPredicate, handler: ServerFunction2): (IntroSpring2.Request) -> ServerFunction2 = {
        if (p.test(it.path)) handler else { r -> IntroSpring2.Response("WRONG REQUEST : $r") }

    }

    fun router2(routes: RouterDsl2.() -> Unit): Collection<(Request) -> ServerFunction2> = RouterDsl2().apply(routes).routes()



}