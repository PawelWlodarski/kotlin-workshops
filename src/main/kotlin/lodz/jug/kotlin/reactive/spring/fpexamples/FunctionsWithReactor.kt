package lodz.jug.kotlin.reactive.spring.fpexamples

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun main(args: Array<String>) {
//    currying()
    curryingWithDao()
}



fun currying(){
    fun simpleStringProcessor(prefix:String) : (String) -> String = { input ->
        "$prefix${input.trim().toUpperCase()}"
    }

    val f=Flux.just("one" , "    two  ", "three")

    val prefix1 = simpleStringProcessor("[DEBUG] ")
    val prefix2 = simpleStringProcessor("[MyPrefix] ")

    println("easy to test : ${prefix1("myText") == "[DEBUG] MYTEXT"} ")

    f.map(prefix1).subscribe(::println)
    f.map(prefix2).subscribe(::println)
}

typealias UserId = String

fun curryingWithDao(){


    fun daoProvider(dao: UserDao) : (UserId) -> Mono<String> = dao::find

    val f=Flux.just("id1","id2","id3")

    val selectUser = daoProvider(MockUserDao)

    println("easyToTest ${selectUser("id1").block()}")

    f.flatMap(selectUser).subscribe(::println)

}

interface UserDao{
    fun find(id:String) : Mono<String>
}

object MockUserDao : UserDao {
    override fun find(id: String): Mono<String>  = Mono.just("mockUser-$id")
}

