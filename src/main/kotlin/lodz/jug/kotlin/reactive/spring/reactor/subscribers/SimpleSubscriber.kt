package lodz.jug.kotlin.reactive.spring.reactor.subscribers

import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber

/**
 * Base Subscriber is a high level wrapper provided by reactor
 */
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