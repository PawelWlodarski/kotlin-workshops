package lodz.jug.kotlin.ktorwork.two.exercises

import io.kotlintest.matchers.string.contain
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DataConversion
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.put
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import lodz.jug.kotlin.ktorwork.two.exercises.Ktor2ExercisesServer.ktor2Module
import org.junit.Test

@KtorExperimentalLocationsAPI
class Module5ExercisesTest {

    @Test
    fun putNewMeetup() = withTestApplication(ktor2Module()) {
        with(handleRequest(HttpMethod.Put, "/create/kotlinIntro?maxPeople=20")) {
            response.status() shouldBe HttpStatusCode.Accepted
            response.content shouldBe "CREATED[topic[kotlinIntro],max[20]]"
        }
    }

    @Test
    fun getAllMeetups() = withTestApplication(ktor2Module()) {
        cleanDb()
        handleRequest(HttpMethod.Put, "/create/java19?maxPeople=50")
        handleRequest(HttpMethod.Put, "/create/scalaIntro?maxPeople=37")
        handleRequest(HttpMethod.Put, "/create/dartInAction")

        with(handleRequest(HttpMethod.Get, "/all")) {
            response.status() shouldBe HttpStatusCode.OK
            response.content should contain("{topic[java19],max[50]}")
            response.content should contain("{topic[scalaIntro],max[37]}")
            response.content should contain("{topic[dartInAction],max[30]}")
        }
    }


    @Test
    fun findMeetup() = withTestApplication(ktor2Module()) {

        handleRequest(HttpMethod.Put, "/create/java19?maxPeople=41")

        with(handleRequest(HttpMethod.Get, "/search/java19")){
            response.status() shouldBe HttpStatusCode.OK
            response.content should contain("""topic[java19],max[41]""")
        }
    }

    @Test
    fun didNotFindMeetup() = withTestApplication(ktor2Module()) {
        cleanDb()
        with(handleRequest(HttpMethod.Get, "/search/java19")){
            response.status() shouldBe HttpStatusCode.NotFound
            response.content should contain("Unable to find java19")
        }
    }


    private fun TestApplicationEngine.cleanDb() = handleRequest(HttpMethod.Post, "/clean")

}


@KtorExperimentalLocationsAPI
object Ktor2ExercisesServer {


    data class Topic(val text: String)

    @Location("/create/{topic}")
    data class Meetup(val topic: Topic, val maxPeople: Int = 30)

    @Location("/search/{topic}")
    data class Search(val topic:Topic)

    private fun displayMeetup(m: Meetup): String = "topic[${m.topic.text}],max[${m.maxPeople}]"

    private var database: List<Meetup> = emptyList()


    fun ktor2Module(): Application.() -> Unit = {
        install(CallLogging) {
            level = org.slf4j.event.Level.DEBUG
        }
        install(Locations)


        install(DataConversion) {
            convert<Topic> {
                TODO() //EXERCISE
            }
        }

        routing {
            //            trace { application.log.debug(it.buildText()) }

            post("/clean"){
                database= emptyList()
            }

            put<Meetup> {
                TODO() //EXERCISE
            }



        }
    }
}