package lodz.jug.kotlin.ktorwork.one.exercises

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import io.kotlintest.shouldBe
import org.amshove.kluent.`should contain`
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqualTo
import org.assertj.core.internal.bytebuddy.utility.RandomString
import org.junit.Test

class KtorExercises1 {

    @Test
    fun `should put number`() {
        val (_, response, result: Result<String, FuelError>) = "http://localhost:${KtorExercises1Server.serverPort}/addNumber/2"
                .httpPost().responseString()


        response.statusCode shouldEqualTo (200)
        result shouldBeInstanceOf (Result.Success::class)
        result.get() shouldBeEqualTo ("added number 2")
    }


    @Test
    fun `default 404 page should be defined`() {
        repeat(5) {
            val random = RandomString.make(5)

            val (_, response: Response, _) = "http://localhost:${KtorExercises1Server.serverPort}/$random"
                    .httpPost().response()

            response.statusCode shouldEqualTo 404
            String(response.data) shouldBeEqualTo "try again later"
        }
    }

    @Test
    fun `returns BadRequest when number can not be parsed`() {
        repeat(5) {
            val random = RandomString.make(5)

            val (_, response: Response, _) = "http://localhost:${KtorExercises1Server.serverPort}/addNumber/$random"
                    .httpPost().responseString()

            response.statusCode shouldEqualTo 400
            String(response.data) `should contain` "unable to retrieve parameter"
        }
    }



    @Test
    fun `removes all data`(){
        val (_, response, result) = "http://localhost:${KtorExercises1Server.serverPort}/all"
                .httpDelete().responseString()

        response.statusCode shouldEqualTo 200
        result.get() shouldBe "all data removed!!!"
    }


    @Test
    fun `stores and retrieves numbers`() {
        //given
        "http://localhost:${KtorExercises1Server.serverPort}/all".httpDelete().response()


        (1 .. 5).map {
            val (_, response: Response, _) = "http://localhost:${KtorExercises1Server.serverPort}/addNumber/$it"
                    .httpPost().response()

            response.statusCode shouldEqualTo 200
        }

        val (_, response, result) = "http://localhost:${KtorExercises1Server.serverPort}/numbers"
                .httpGet().responseString()

        response.statusCode shouldBe 200
        result.get() shouldBe """{"numbers":[1,2,3,4,5]}"""
    }


    data class InitRequest(val numbers:List<Int>)

    @Test
    fun `send and receive json`() {
        val payload=Gson().toJson(InitRequest(listOf(10,11,12)))

        val (_, resetResponse,resetResult)="http://localhost:${KtorExercises1Server.serverPort}/reset"
                .httpPut().jsonBody(payload).responseString()


        resetResponse.statusCode shouldBe 200
        resetResult.get() shouldBe "database reset"

        val (_, selectAll, selectAllResult) = "http://localhost:${KtorExercises1Server.serverPort}/numbers"
                .httpGet().responseString()

        selectAll.statusCode shouldBe 200
        selectAllResult.get() shouldBe """{"numbers":[10,11,12]}"""

    }
}

