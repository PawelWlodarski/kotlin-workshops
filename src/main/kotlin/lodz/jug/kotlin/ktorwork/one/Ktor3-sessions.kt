package lodz.jug.kotlin.ktorwork.one

import io.ktor.application.*
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*

fun main(args: Array<String>) {

   val config= applicationEngineEnvironment{
        module {
            routing {
                get("/sessionData"){
                    val data=call.sessions.get<Module3Session>()
                    call.respondText("stored session data : $data")
                }
            }

            install(Sessions){
                cookie<Module3Session>("KTOR-SESSION-COOKIE"){
                    //import io.ktor.util.hex
//                    val secretSignKey = hex("000102030405060708090a0b0c0d0e0f")
//                    transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
                }
            }
        }

       module {
           module2()
       }

       connector {
           host = "0.0.0.0"
           port = 8080
       }
    }


    //SHOW SEMI-DEPENDANT CONFIGURATION FOR NETTY
    embeddedServer(Netty, config).start(true)
}

data class Module3Session(val data:String)

//This 'set' method is very messy, you need to import specific signature!!!
fun Application.module2() {

    suspend fun setSession(call: ApplicationCall) {
        val param=call.parameters["p"] ?: ""
          call.sessions.set(Module3Session("some user data $param"))
          call.respondText("Module2 set session with param=$param")

    }

    routing {
        //LOGGING ON HOW TRACE IS MATCH!!!
        trace { application.log.debug(it.buildText()) }

        method(HttpMethod.Get){
            route("/m2"){
                optionalParam("p"){
                        handle {
                            setSession(call)
                        }
                }
            }
        }

    }


}