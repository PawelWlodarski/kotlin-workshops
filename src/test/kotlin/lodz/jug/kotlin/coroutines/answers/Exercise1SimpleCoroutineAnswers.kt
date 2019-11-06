package lodz.jug.kotlin.coroutines.answers

import arrow.core.Option
import arrow.core.getOrElse
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import lodz.jug.kotlin.coroutines.ExerciseLogger
import lodz.jug.kotlin.coroutines.answers.CDownloaderAnswer1.Image
import lodz.jug.kotlin.coroutines.answers.CDownloaderAnswer1.MISSING_IMAGE
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.math.BigInteger
import java.util.concurrent.Executors


class Exercise1SimpleCoroutineAnswers {


    @Test
    fun exercise1() = runBlockingTest {
        val result = CDownloaderAnswer1.downloadAllInMax1Second()

        result.asIterable() shouldContainSame arrayOf(
                Image("img1"),
                Image("img2"),
                Image("img3"),
                MISSING_IMAGE
        )
    }


    @Test
    fun exercise2() {
        runBlocking {
            val result = withTimeout(2000) {
                CEngineAnswer1.calculateSum()
            }
            //observe how easy it is to wait for async result and use it in a matcher
            result shouldEqual BigInteger.valueOf(1799815000)
        }
    }
}

//to use "log" set JVm param
// -Dkotlinx.coroutines.debug or -ea
//

object CDownloaderAnswer1 {

    data class Image(val content: String)

    val MISSING_IMAGE = Image("NOT_FOUND")

    fun downloadAllInMax1Second(): Sequence<Image> {

        val url1 = "http://image1"
        val url2 = "http://image2"
        val url3 = "http://image3"
        val url4 = "http://image4"

        val job1 = GlobalScope.async { Downloader.download(url1) }
        val job2 = GlobalScope.async { Downloader.download(url2) }
        val job3 = GlobalScope.async { Downloader.download(url3) }
        val job4 = GlobalScope.async { Downloader.download(url4) }


        return runBlocking {
            withTimeout(1000L) {
                sequenceOf(job1.await(), job2.await(), job3.await(), job4.await())
                        .map { it.getOrElse { MISSING_IMAGE } }
            }

        }
    }

    object Downloader {
        private val images = mapOf(
                "http://image1" to Image("img1"),
                "http://image2" to Image("img2"),
                "http://image3" to Image("img3")
        )


        fun download(url: String): Option<Image> {
            Thread.sleep(500)
            return images[url]?.let { Option.just(it) } ?: Option.empty()
        }
    }
}

object CEngineAnswer1 {
    data class CalculationTask1(val from: BigInteger, val to: BigInteger)


    private val mainDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val workersDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()


    suspend fun calculateSum(): BigInteger {
        val tasks = listOf(
                (1 to 5000),
                (5001 to 10000),
                (10001 to 20000),
                (20001 to 30000),
                (30001 to 40000),
                (40001 to 50000),
                (50001 to 60000)
        ).map { (from, to) -> CalculationTask1(BigInteger.valueOf(from.toLong()), BigInteger.valueOf(to.toLong())) }

        return withContext(mainDispatcher + CoroutineName("main execution")) {
            ExerciseLogger.log("starting main calculations")
            tasks
                    .map { task -> GlobalScope.async { Calculator.calculateSumFor(task) } }
                    .map { it.await() }
                    .reduce(BigInteger::add)
        }


    }

    object Calculator {
        suspend fun calculateSumFor(t: CalculationTask1): BigInteger =
                withContext(workersDispatcher + CoroutineName("task calculations")) {
                    Thread.sleep(500)
                    ExerciseLogger.log("calculating task for : $t")
                    generateSequence(t.from, { it.inc() })
                            .take(t.to.minus(t.from).toInt())
                            .reduce(BigInteger::add)
                }
    }
}