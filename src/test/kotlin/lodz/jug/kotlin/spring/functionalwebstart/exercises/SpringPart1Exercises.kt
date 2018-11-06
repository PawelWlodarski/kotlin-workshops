package lodz.jug.kotlin.spring.functionalwebstart.exercises

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.adapter.WebHttpHandlerBuilder
import reactor.core.publisher.Mono
import reactor.ipc.netty.http.server.HttpServer


fun main(args: Array<String>) {
    fun startReactor(host:String="localhost", port:Int=9050) {
        //reactory library
        val server = HttpServer.create(port)
        //spring library - adapter
        val httpHandler = WebHttpHandlerBuilder.applicationContext(SpringPart1Exercises.ctx).build()
        server.startAndAwait(ReactorHttpHandlerAdapter(httpHandler))
    }


    startReactor()
}


object SpringPart1Exercises{

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
            TODO() //<---------EXERCISE
        }
        "/datastore".nest {
            PUT("/user/{id}"){r ->
               TODO() //
            }
            GET("/users"){
                TODO() //<---------EXERCISE
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