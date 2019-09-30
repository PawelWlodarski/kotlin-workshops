package lodz.jug.kotlin.reactive.croutines.javaexample

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val l=SomethingWithCoroutines.start("someText")
    SomethingWithCoroutines.logic(l)
}

object SomethingWithCoroutines{

    suspend fun start(s:String):Int{
        delay(500)
        return s.length

    }

    suspend fun logic(length:Int){
        delay(10)
        println("length : $length")
    }
}

