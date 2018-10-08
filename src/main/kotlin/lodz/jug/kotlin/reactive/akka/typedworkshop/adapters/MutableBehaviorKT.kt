package lodz.jug.kotlin.reactive.akka.typedworkshop.adapters

import akka.actor.typed.ActorContext
import akka.actor.typed.Behavior
import akka.actor.typed.ExtensibleBehavior
import akka.actor.typed.Signal

abstract class MutableBehaviorKT<T> : ExtensibleBehavior<T>() {

    abstract override fun receive(ctx: ActorContext<T>, msg: T): Behavior<T>

    override fun receiveSignal(ctx: ActorContext<T>, msg: Signal): Behavior<T> = this@MutableBehaviorKT
}