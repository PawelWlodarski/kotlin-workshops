package lodz.jug.kotlin.akka.typed.hello

import akka.typed.*
import akka.typed.scaladsl.Actor


class HelloScala1 : Actor.MutableBehavior<HelloScala1.Protocol.Command>() {

    private var greeting = "hello"

    override fun onMessage(msg: Command): Behavior<Command> {
        when (msg) {
            Command.ScalaGreet -> println(greeting)
            is Command.ScalaWhoToGreet -> greeting = "hello ${greeting}"
        }
        return this
    }

    companion object Protocol {
        sealed class Command {
            object ScalaGreet : Command()
            data class ScalaWhoToGreet(val who: String) : Command()
        }

        val greeterBehaviour: Behavior<Command> = Actor.mutable<Command> { ctx -> HelloScala1() }
    }
}

sealed class Command

object ScalaGreet : Command()
data class ScalaWhoToGreet(val who: String) : Command()

object HelloScala2 {
    val greeterBehavior: Behavior<Command> = greeterBehavior(currentGreeting = "hello")

    private fun greeterBehavior(currentGreeting: String): Behavior<Command> =
            Actor.immutable { _, msg ->
                when (msg) {
                    is ScalaWhoToGreet ->
                        greeterBehavior("hello, ${msg.who}")
                    ScalaGreet -> {
                        println(currentGreeting)
                        Actor.same<Command>()
                    }

                }
            }

}

fun main(args: Array<String>) {
    val root = Actor.deferred<Nothing> { ctx ->
        //scala default parameters not working in kotlin -> props
        val greeter: ActorRef<Command> = ctx.spawn(HelloScala2.greeterBehavior, "greeter", Props.empty())
        greeter.tell(ScalaWhoToGreet("ScalaExample"))
        greeter send ScalaGreet
        Actor.empty()
    }


//    `ActorSystem$`.`MODULE$`.apply()  //pure scala API with default parameters not recognised by Kotlin
    ActorSystem.create(root,"HelloWorld") // create is actually Java API

}

infix fun <T> ActorRef<T>.send(cmd:T) = this.tell(cmd)