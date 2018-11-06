package lodz.jug.kotlin.spring.functionalwebstart.answers

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono
import reactor.ipc.netty.http.server.HttpServer


fun main(args: Array<String>) {
    fun startReactor(host:String="localhost", port:Int=9050) {
        //reactory library
        val server = HttpServer.create(port)
        //spring library - adapter
        val httpHandler = WebHttpHandlerBuilder.applicationContext(SpringPart1Answers.ctx).build()
        server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
    }


    startReactor()
}


object SpringPart1Answers{

    //This will be used in test
    const val ERROR_MESSAGE = "repo_error"

    interface Repo{
        fun put(id:Int) : Mono<Int>
        fun get() : Mono<Collection<String>>
    }

    //Repo uses async types
    class ExerciseRepo : Repo {

        private var store = mapOf<Int,String>()

        override fun get(): Mono<Collection<String>> = Mono.just(store.values)

        override fun put(id: Int) : Mono<Int> {
            if(id==3) return Mono.error(RuntimeException(ERROR_MESSAGE))

            store += (id to "User$id")
            return Mono.just(id)
        }

    }

    private fun generateResponseBody() = Mono.just("Hello exercise 1")

    private fun createRouting(repo: Repo) = router {
        GET("/exercise1get"){_ ->
            ServerResponse.ok().body(generateResponseBody(), String::class.java)
        }
        "/datastore".nest {
            PUT("/user/{id}"){r ->
                Mono.justOrEmpty(r.pathVariable("id"))
                        .map(String::toInt)
                        .flatMap(repo::put)
                        .flatMap{ ServerResponse.ok().syncBody("Successfully created user with id=$it")}
                        .onErrorResume {error -> ServerResponse.badRequest().syncBody("ERROR:${error.message}")}
            }
            GET("/users"){
                ServerResponse.ok().body(repo.get().map { it.joinToString(separator = "," ) } , String::class.java)
            }
        }
    }



    val ctx = GenericApplicationContext{
        beans {
            bean<ExerciseRepo>()
            bean("webHandler"){
                val repo = ref<ExerciseRepo>()
                RouterFunctions.toWebHandler(createRouting(repo)) //<- change our routing into spring component
            }
        }.initialize(this)
        refresh()
    }


}