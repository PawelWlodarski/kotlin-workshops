package lodz.jug.kotlin.akka.untyped.kotlin

import akka.actor.AbstractActor
import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.Adapter
import kotlin.reflect.KClass





object ActorsystemKT{
    operator fun invoke(name:String) = akka.actor.ActorSystem.create(name)
}

object PropsKt{
     operator fun <T : Any> invoke(clazz: KClass<T>, vararg args: Any) =
             akka.actor.Props.create(clazz.java,*args)
}

//Typed conversions
fun <T> AbstractActor.ActorContext.spawn(b:Behavior<T>,name:String):akka.actor.typed.ActorRef<T> =
        Adapter.spawn(this,b,name)

fun <T> AbstractActor.ActorContext.watchTyped(actorRef: ActorRef<T>) = Adapter.watch(this,actorRef)

fun  <T> akka.actor.ActorRef.toTyped() : ActorRef<T> = Adapter.toTyped<T>(this)
