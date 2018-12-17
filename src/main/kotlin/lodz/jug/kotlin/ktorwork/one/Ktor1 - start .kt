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

fun main(args: Array<String>) {
//    adHocConfig()
    Routes.startServerWithCustomModule()
}


//This example show quick server configuration
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

//This one shows modularisation
object Routes{
    fun startServerWithCustomModule() =
            embeddedServer(Netty,port = 8080, module = Application::myModule2).start(wait = true)
}

//It has to be defined on a top level so extension is visible
fun Routing.routes1() {
    get("/"){
        call.respondText("I'm in the module in composite routing!", ContentType.Text.Html)
    }
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


//It has to be defined on a top level so extension is visible
fun Application.myModule1(){
        routing {routes1()}
}

//modular configuration
fun Application.myModule2(){
    routing {routes2()}

    intercept(ApplicationCallPipeline.Call) {
        if (call.request.uri == "/globalIntercept") {
            call.respondText("Globally intercepted")
        }
    }

    //Order not important
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

