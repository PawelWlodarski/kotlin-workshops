package lodz.jug.kotlin.akka.typed.kotlin

import akka.actor.typed.Behavior
import akka.actor.typed.Signal
import akka.actor.typed.javadsl.Behaviors

abstract class MutableBehaviorKT<T> : Behaviors.MutableBehavior<T>() {

    abstract fun onMessage(msg: T): Behavior<T>

    override fun createReceive(): Behaviors.Receive<T> = object : Behaviors.Receive<T> {
        override fun receiveMessage(msg: T): Behavior<T> = onMessage(msg)
        override fun receiveSignal(msg: Signal?): Behavior<T> = this@MutableBehaviorKT
    }
}
