package lodz.jug.kotlin.akka.typed.kotlin

import akka.typed.Behavior
import akka.typed.Signal
import akka.typed.javadsl.Actor

abstract class MutableBehaviorKT<T> : Actor.MutableBehavior<T>() {

    abstract fun onMessage(msg: T): Behavior<T>

    override fun createReceive(): Actor.Receive<T> = object : Actor.Receive<T> {
        override fun receiveMessage(msg: T): Behavior<T> = onMessage(msg)
        override fun receiveSignal(msg: Signal?): Behavior<T> = this@MutableBehaviorKT
    }
}
