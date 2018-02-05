package lodz.jug.kotlin.akka.typed.kotlin

import akka.typed.ActorRef

infix fun <T> ActorRef<T>.send(cmd:T) = this.tell(cmd)