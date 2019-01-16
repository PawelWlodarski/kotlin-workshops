package lodz.jug.kotlin.ktorwork.two

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CALL_ID_DEFAULT_DICTIONARY
import io.ktor.features.CallId
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.request.header
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.atomicfu.atomic
import lodz.jug.kotlin.ktorwork.two.Module5Units.callId
import lodz.jug.kotlin.ktorwork.two.Module5Units.forms
import lodz.jug.kotlin.ktorwork.two.Module5Units.info
import lodz.jug.kotlin.ktorwork.two.Module5Units.params
import org.springframework.http.HttpStatus
import io.ktor.locations.get as locationGet


fun main(args: Array<String>) {

}


fun Application.module5() {

    install(Locations)

    routing {
        //            trace { application.log.debug(it.buildText()) }
        info()
        params()
        forms()
        callId()
    }


    install(CallId) {
        retrieve { call: ApplicationCall ->
            call.request.header(HttpHeaders.XRequestId)
        }

        val counter = atomic(0)
        generate { "callId-${counter.getAndIncrement()}" }

        verify { callId: String ->
            callId.isNotEmpty()
        }

        header(Module5Units.CALL_ID_HEADER)

    }
}

@KtorExperimentalLocationsAPI
object Module5Units {

    const val CALL_ID_HEADER = "customCallId"

    fun Routing.info() = get("/info") {
        call.respondText("info result")
    }

    @Location("/params")
    data class ParamsEnpoint(val p1: Int, val p2: String, val toUpper: Boolean)

    fun Routing.params() = locationGet<ParamsEnpoint> { ps ->
        val p2value = if (ps.toUpper) ps.p2.toUpperCase() else ps.p2

        call.respondText("passed {p1 : ${ps.p1}, p2 : $p2value }")
    }


    fun Routing.forms() = post("/forms1") {
        val params=call.receiveParameters()
        call.respondText("FORM:[param1=${params["param1"]},param2=${params["param2"]}]")
    }


    fun Routing.callId() = get("/callId") {
        val response=call.request.headers[CALL_ID_HEADER]?.let {h -> "send call id : $h" } ?: "Id auto generated"
        call.respondText(response)

    }
}