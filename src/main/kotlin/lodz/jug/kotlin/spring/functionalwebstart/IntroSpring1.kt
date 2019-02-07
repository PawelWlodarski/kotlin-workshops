package lodz.jug.kotlin.spring.functionalwebstart

import lodz.jug.kotlin.Displayer

@Suppress("MoveLambdaOutsideParentheses")
fun main() {
    Displayer.header("  SPRING PREPARATION - ROUTER DSL")

    //CUSTOM STRUCTURE SYNTAX
    Displayer.title("EXAMPLE 1 : normal invocation")
    val response = singleRoute("/hello","requestExample1", {request -> "response for $request"})
    Displayer.section("Example 1 response :  ",response)

    Displayer.title("EXAMPLE 2 : structured invocation")
    val r2=singleRoute("/hello","requestExample1"){request ->
        "and this is structured response for $request"
    }
    Displayer.section("Example 2 response :  ",r2)

    //dsl builder
    Displayer.title("EXAMPLE 3 : dsl builder with EXPLICIT it")
    val buildingResult=buildRouterWitIt {
        it.route("/hello",{r -> "response for $r"})
        it.route("/info",{r -> "response for $r"})
    }

    Displayer.section("building results for EXAMPLE 3 : $buildingResult")


    Displayer.title("EXAMPLE 4 : the real DSL with implicit receiver")

    val dslResult= buildRouter {
        route("/index.html",{r -> "response for $r"})
        route("/register",{"response for $it"})
    }

    Displayer.section("building results for EXAMPLE 4 : $dslResult")
}


/**
 * 1,2 custom structure
 */
typealias Request = String
typealias Response = String

fun singleRoute(path:String, request:String, handler : (Request) -> Response):String {
    Displayer.section("handling call on path",path)
    return handler(request)
}


/**
 * 3 EXPLICIT BUILDER
 */
fun buildRouterWitIt(buildLogic : (CustomRoutingDsl) -> Unit):Routes{
    val dsl = CustomRoutingDsl()
    buildLogic(dsl)
    return dsl.toRoutes()
}

/**
 * 3 IMPLICIT BUILDER
 */
fun buildRouter(buildLogic : CustomRoutingDsl.() -> Unit):Routes{
    val dsl = CustomRoutingDsl()
//    buildLogic(dsl)
    dsl.buildLogic() // LOCAL EXTENSION METHOD - THIS WILL BE IMPORTANT
    return dsl.toRoutes()
}


/**
 * 3 DSL STRUCTURE
 * ** also small alias example
 */
typealias Handler1 = (Request) -> Response

class CustomRoutingDsl{
    private var routes = emptyMap<String,Handler1>()

    fun route(path:String,h:Handler1){
        routes = routes + (path to h)
    }

    fun toRoutes() = Routes(routes)
}

data class Routes(val  routes : Map<String,Handler1>)