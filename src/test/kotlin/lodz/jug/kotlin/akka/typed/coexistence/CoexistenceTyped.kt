package lodz.jug.kotlin.akka.typed.coexistence

import akka.actor.UntypedAbstractActor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.ActorContext
import lodz.jug.kotlin.akka.typed.kotlin.*
import lodz.jug.kotlin.akka.untyped.kotlin.*
import java.util.concurrent.TimeUnit

//COMMON ERROR IN KOTLIN WHEN YOU KNOW SCALA fun={} VS fun={}
fun main(args: Array<String>) {

    example1()
}

fun example1() {
    val system=ActorsystemKT("sys")
    system.actorOf(CoexistenceUntyped.props(),"first")
    TimeUnit.SECONDS.sleep(1)
    system.terminate()
}

class CoexistenceUntyped : UntypedAbstractActor(){ //instead of AbstractActor

    private val second: ActorRef<Command> = context.spawn(CoexistenceTyped.behavior,"second")

    override fun preStart() {
        println("starting...")
        context.watchTyped(second)
        second send Ping(self.toTyped())
    }

    override fun onReceive(message: Any?) = when(message){
        Pong -> {
            println("$self got Pong from ${sender()}")
        }
        else -> context.system.deadLetters().tell(message,self)
    }

    companion object {
        fun props()= PropsKt(CoexistenceUntyped::class)
    }
}

sealed class Command
data class Ping(val replyTo: ActorRef<Pong>) :Command()
object Pong

object CoexistenceTyped{
    val behavior:Behavior<Command> = Behaviors.immutable { ctx,msg ->
        when(msg){
            is Ping -> onPing(ctx,msg)
        }
    }

    private fun onPing(ctx: ActorContext<Command>, msg: Ping):Behavior<Command>  {
        println("${ctx.self} got Ping from ${msg.replyTo}")
        msg.replyTo send Pong
        return Behaviors.same<Command>()
    }

}

data class Ping2(val replyTo:ActorRef<Command2>)
sealed class Command2
object Pong2 :Command2()

object CoexistenceTyped2 {
    val behavior:Behavior<Command2> = Behaviors.setup{ctx ->
        val second: akka.actor.ActorRef = ctx.actorOf(CoexistenceUntyped2.props(),"second")
        ctx.watchUntyped(second)

        second.tell(Ping2(ctx.self),ctx.self.toUntyped())

        val first: Behavior<Command2> = Behaviors.immutable<Command2>{ ctx, msg ->
            when(msg){
                Pong2 -> onPong(ctx, second)
            }
        }


        first

    }

    private fun onPong(ctx: ActorContext<Command2>, second: akka.actor.ActorRef): Behavior<Command2> {
        println("${ctx.self} got Pong")
        // context.stop is an implicit extension method
        ctx.stopUntyped(second)
        return Behaviors.same()
    }
}


class CoexistenceUntyped2 : UntypedAbstractActor() {

    override fun onReceive(message: Any?) = when(message){
        is Ping2 -> {
            println("$self got Pong from ${sender()}")
            message.replyTo send Pong2
        }
        else -> context.system.deadLetters().tell(message,self)
    }


    companion object {
        fun props() = PropsKt(CoexistenceUntyped2::class)
    }

}