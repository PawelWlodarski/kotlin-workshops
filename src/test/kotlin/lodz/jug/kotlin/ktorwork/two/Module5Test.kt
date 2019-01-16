package lodz.jug.kotlin.ktorwork.two

import io.kotlintest.shouldBe
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.formUrlEncode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class Module5Test {

    @Test
    fun getEndpoint()=withTestApplication(Application::module5) {
        with(handleRequest(HttpMethod.Get, "/info")) {
            //<- this one is already defined in the engine
            response.status() shouldBe io.ktor.http.HttpStatusCode.OK
            response.content shouldBe "info result" //change this one
        }
    }


    @Test
    fun getParameters()=withTestApplication(Application::module5) {
        with(handleRequest(HttpMethod.Get, "/params?p1=4&p2=aaa&toUpper=true")) {
            response.content shouldBe "passed {p1 : 4, p2 : AAA }"
        }


        with(handleRequest(HttpMethod.Get, "/params?p1=4&p2=aaa&toUpper=false")) {
            response.content shouldBe "passed {p1 : 4, p2 : aaa }"
        }

    }


    @Test
    fun receiveFormWithParameters() = withTestApplication(Application::module5){
        val call = handleRequest(HttpMethod.Post, "/forms1") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("param1" to "value1", "param2" to "value2").formUrlEncode())
        }

        call.response.content shouldBe "FORM:[param1=value1,param2=value2]"
    }


    @KtorExperimentalLocationsAPI
    @Test
    fun generateCallId() =withTestApplication(Application::module5) {
        with(handleRequest(HttpMethod.Get, "/callId")) {
            //<- this one is already defined in the engine
            response.headers[Module5Units.CALL_ID_HEADER] shouldBe "callId-0"
            response.content shouldBe "Id auto generated"
        }

        with(handleRequest(HttpMethod.Get, "/callId") {
            addHeader(Module5Units.CALL_ID_HEADER,"69")
            //<- this one is already defined in the engine
        }){
            response.headers[Module5Units.CALL_ID_HEADER] shouldBe "69"
            response.content shouldBe "send call id : 69"
        }


    }

}