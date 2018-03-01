package lodz.jug.kotlin.akka.typed.hello

import akka.typed.ActorRef
import akka.typed.ActorSystem
import akka.typed.Behavior
import akka.typed.javadsl.Actor
import lodz.jug.kotlin.akka.typed.kotlin.MutableBehaviorKT
import lodz.jug.kotlin.akka.typed.kotlin.send


class Greeter1 : MutableBehaviorKT<Greeter1.Protocol.Command>() {

    private var greeting = "hello"

    override fun onMessage(msg: Command): Behavior<Command> {
        when (msg) {
            Command.Greet -> println(greeting)
            is Command.WhoToGreet -> greeting = "hello ${greeting}"
        }
        return this
    }

    companion object Protocol {
        sealed class Command {
            object Greet : Command()
            data class WhoToGreet(val who: String) : Command()
        }

        val greeterBehaviour: Behavior<Command> = Actor.mutable<Command> { ctx -> Greeter1() }
    }
}

sealed class CommandJava
data class WhoToGreet(val who: String) : CommandJava()
object Greet : CommandJava()


object Greeter2 {

    val greeterBehavior: Behavior<CommandJava> = greeterBehaviour(currentGreeting = "hello")

    private fun greeterBehaviour(currentGreeting: String): Behavior<CommandJava> =
            Actor.immutable<CommandJava> { _, msg ->
                when (msg) {
                    Greet -> {
                        println(currentGreeting)
                        Actor.same()
                    }
                    is WhoToGreet -> greeterBehaviour("hello ${msg.who}")

                }
            }
}

fun main(args: Array<String>) {
    val root: Behavior<Nothing> = Actor.deferred<Nothing> { ctx ->
        val greeter: ActorRef<CommandJava> = ctx.spawn(Greeter2.greeterBehavior, "greeter")

        greeter send WhoToGreet("Java")
        greeter send Greet

        Actor.empty()
    }

    ActorSystem.create(root, "HelloWorld")
}