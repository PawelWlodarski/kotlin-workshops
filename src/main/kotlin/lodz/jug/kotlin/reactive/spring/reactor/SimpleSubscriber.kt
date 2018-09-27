package lodz.jug.kotlin.reactive.spring.reactor

import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber

class SimpleSubscriber<T> : BaseSubscriber<T>(){
    override fun hookOnSubscribe(subscription: Subscription?) {
        println("Simple Subscriber subscribed")
        request(1)
    }

    override fun hookOnNext(value: T) {
        println("requested next $value")
        request(1)
    }
}