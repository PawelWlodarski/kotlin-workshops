package lodz.jug.kotlin.reactive.akka.typedworkshop

import akka.NotUsed
import akka.actor.typed.ActorContext
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Behaviors
import kategory.Option
import kategory.getOrElse
import lodz.jug.kotlin.Displayer
import lodz.jug.kotlin.reactive.akka.typedworkshop.adapters.MutableBehaviorKT
import lodz.jug.kotlin.reactive.akka.typedworkshop.adapters.send
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    Displayer.header(" KOTLIN AKKA TYPED - MUTABLE ACTOR")
    //everything in package akka.actor.typed or akka.actor.typed.javaqdsl (since 2.5.11)
    val mutableBehavior= akka.actor.typed.javadsl.Behaviors.setup<Part1Command>{MutableTyped()}

    //main thread
    Displayer.section("starting in thread ${Thread.currentThread().name}")

    //special setup behavior
    val main:Behavior<NotUsed> =Behaviors.setup { ctx ->
        //some thread from actor pool
        Displayer.section("setup in thread ${Thread.currentThread().name}")
        val helloActor=ctx.spawn(mutableBehavior,"HelloActor")
        val devNull= ctx.spawn(Behaviors.setup<Any> { DevNull() },"DevNull")
        //sender is not mandatory anymore, if you want to have sender you need to add it to the protocol
        helloActor send Part1Hello("mainFunction",replyTo = devNull)
//        helloActor send "Forbidden"
//      helloActor send 1
        helloActor send Part1Goodbye
        //stop system after all
        Behaviors.stopped()
    }


    val system= ActorSystem.create(main,"HelloSystem")
    Await.result(system.whenTerminated(),  Duration.apply(3, TimeUnit.SECONDS))
}


sealed class Part1Command
data class Part1Hello(val who:String, val replyTo: ActorRef<in String>) : Part1Command()
object Part1Goodbye : Part1Command()


class MutableTyped : MutableBehaviorKT<Part1Command>(){

    private var state : Option<String> = Option.None
    private var sender : ActorRef<in String>? = null

    override fun receive(ctx: ActorContext<Part1Command>, msg: Part1Command): Behavior<Part1Command>  =
            when(msg){
                is Part1Hello ->{
                    state = Option.Some(msg.who)
                    sender = msg.replyTo
                    //Expect some thread from an actor pool
                    println("Hello to ${msg.who} in thread ${Thread.currentThread().name}")
                    this
                }
                is Part1Goodbye -> {
                    println("""bye to ${state.getOrElse {  "UNKNOWN" }} """)
                    sender send "bye to ${state.getOrElse{"UNKNOWN"}}"
                    Behaviors.stopped() // actor died!!!
                }

            }
}

class DevNull : MutableBehaviorKT<Any>(){
    override fun receive(ctx: ActorContext<Any>, msg: Any): Behavior<Any> {
        //some thread from actor pool
        Displayer.section("DEV NULL $msg in thread ${Thread.currentThread().name}")
        return Behaviors.same()
    }

}