package lodz.jug.kotlin.reactive.croutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

object ExampleContexts {

    /**
     * Factory used to generate custom names for threads
     */
    private fun myThreadFactory(id:String) = object : ThreadFactory {
        private val counter = AtomicInteger()

        override fun newThread(r: Runnable): Thread =
                Thread(r).apply {
                    val counterValue = counter.getAndIncrement()
                    name = "$id-$counterValue"
                }

    }

    private val uiThreadPool = Executors.newSingleThreadExecutor(myThreadFactory("myMain"))
    private val ioThreadPool = Executors.newFixedThreadPool(4, myThreadFactory("myIO"))
    val uiScope = CoroutineScope(uiThreadPool.asCoroutineDispatcher())
    val ioScope = CoroutineScope(ioThreadPool.asCoroutineDispatcher())
}