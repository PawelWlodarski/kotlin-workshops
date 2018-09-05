package lodz.jug.kotlin.reactive.akka.typedworkshop.adapters

import akka.actor.typed.ActorRef


infix fun <T> ActorRef<T>?.send(msg:T) : Unit =
        if(this == null) throw RuntimeException("Actor Is Null")
        else this.tell(msg)