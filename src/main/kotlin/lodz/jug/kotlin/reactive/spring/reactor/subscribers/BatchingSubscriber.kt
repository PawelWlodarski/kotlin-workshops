package lodz.jug.kotlin.reactive.spring.reactor.subscribers

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class BatchingSubscriber<A>(private val batch:Long) : Subscriber<A>{

    private var count:Long = 0
    private lateinit var subscription:Subscription

    override fun onSubscribe(subscription: Subscription) {
        println("BatchingSubscriber subscribed")
        this.subscription=subscription
        subscription.request(batch)
    }

    override fun onNext(element: A) {
        count += 1
        if(count>=batch) {
            count=batch
            subscription.request(batch)
        }
    }


    override fun onError(t: Throwable) {
        println("Batch Subscriber Failed ${t.message}")
    }


    override fun onComplete() {
        println("BatchSubscriber Completed")
    }

}