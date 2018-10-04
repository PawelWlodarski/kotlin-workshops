package lodz.jug.kotlin.reactive.spring

object ThreadOps {


    fun printThreadName() =  println("In Thread : ${Thread.currentThread().name}")
    fun threadName() = Thread.currentThread().name

}