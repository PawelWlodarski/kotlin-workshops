package lodz.jug.kotlin.ktorwork.one

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/**
 * adHocConfig - defines configuration in one place with use of anonymous functions
 *
 */
fun main(args: Array<String>) {
//    adHocConfig()
    Routes.startServerWithCustomModule()
}


/**
 *
 * embeddedServer - the last arguemnt is a lambda with Application receiver - this is an entry point for DSL
 *
 * routing - check implementation. routing is an extension method of an 'Application' so
 * you can call it only when you have an Application instance available in the scope
 *
 * get - is an extension method on a Route instance
 *
 * get receive PipelineInterceptor which is an alias for
 *
 * suspend io.ktor.util.pipeline.PipelineContext<TSubject, TContext>.(TSubject) -> kotlin.Unit
 *
 *
 * and finally in the pipeline we have  access to current call:
 *
 * inline val PipelineContext<*, ApplicationCall>.call: ApplicationCall get() = context
 *
 */
private fun adHocConfig() {
    val server = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("HelloWorld", ContentType.Text.Html)
            }
        }
    }

    server.start(wait = true)
}

/**
 * note here that we use already defined myModule2. This approach decople application structure from the way it is started
 * There is also myModule1 defined <- start from that one
 */
object Routes{
    fun startServerWithCustomModule() =
            embeddedServer(Netty,port = 8080, module = Application::myModule1).start(wait = true)
}


fun Routing.routes2() {
    get("/"){
        call.respondText("This is not intercepted", ContentType.Text.Html)
    }.intercept(ApplicationCallPipeline.Call){
            this.call.respondText("this one is intercepted")
    }

    get("/error"){
        throw RuntimeException("this one should be intercepted by StatusPages")
    }
}


/**
 * A module definition.
 * It has to be defined on a top level so extension is visible
 *
 * Here you have also an illustration of decoupled routes.
 *
 */
fun Application.myModule1(){
        routing {routes1()}
}

//It has to be defined on a top level so extension is visible
fun Routing.routes1() {
    get("/"){
        call.respondText("I'm in the module in composite routing!", ContentType.Text.Html)
    }
}


/**
 * second example o a modular configuration.
 * This time we added additional configuration to the module.
 *
 * install section will add 'plugins/traits' or something like this to the module
 *
 * 'StatusPage' plugin adds common handlers for particular exceptions or statuses
 *
 */
fun Application.myModule2(){
    routing {routes2()}

    intercept(ApplicationCallPipeline.Call) {
        if (call.request.uri == "/globalIntercept") {
            call.respondText("Globally intercepted")
        }
    }

    //Order inside is not important - it is standard builder
    install(StatusPages){
        exception<IllegalAccessException> {
            call.respond(HttpStatusCode.Forbidden)
        }
        exception<Throwable>{cause ->
            val message =  " caused by :   ${cause.message ?: cause.toString()}"
           call.respond(HttpStatusCode.InternalServerError,message)
           //throw cause - if you want to log error by default mechanism
        }

        //STATUS - you can mix matchers
        status(HttpStatusCode.NotFound){
            call.respond(TextContent("NOT FOUND :( $it",ContentType.Text.Plain,it))
        }


    }
}

