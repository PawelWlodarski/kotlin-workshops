package lodz.jug.kotlin.akka.typed.hello

import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import lodz.jug.kotlin.akka.typed.kotlin.send


class HelloScala1 : Behaviors.MutableBehavior<HelloScala1.Protocol.Command>() {

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

        val greeterBehaviour: Behavior<Command> = Behaviors.mutable<Command> { ctx -> HelloScala1() }
    }
}

sealed class Command

object ScalaGreet : Command()
data class ScalaWhoToGreet(val who: String) : Command()

object HelloScala2 {
    val greeterBehavior: Behavior<Command> = greeterBehavior(currentGreeting = "hello")

    private fun greeterBehavior(currentGreeting: String): Behavior<Command> =
            Behaviors.immutable { _, msg ->
                when (msg) {
                    is ScalaWhoToGreet ->
                        greeterBehavior("hello, ${msg.who}")
                    ScalaGreet -> {
                        println(currentGreeting)
                        Behaviors.same<Command>()
                    }

                }
            }

}

fun main(args: Array<String>) {
    val root = Behaviors.setup<Nothing> { ctx ->
        //scala default parameters not working in kotlin -> props
        val greeter: ActorRef<Command> = ctx.spawn(HelloScala2.greeterBehavior, "greeter", Props.empty())
        greeter.tell(ScalaWhoToGreet("ScalaExample"))
        greeter send ScalaGreet
        Behaviors.empty()
    }


//    `ActorSystem$`.`MODULE$`.apply()  //pure scala API with default parameters not recognised by Kotlin
    ActorSystem.create(root,"HelloWorld") // create is actually Java API

}
