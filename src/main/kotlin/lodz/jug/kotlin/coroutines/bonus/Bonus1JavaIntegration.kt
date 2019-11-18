package lodz.jug.kotlin.coroutines.bonus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

object Bonus1JavaIntegration{
    suspend fun function1():Int{
        println("calling function 1")
        delay(100)
        return 1
    }

    suspend fun function2(value:Int):Int{
        println("calling function 2")
        delay(1500)
        return value+1
    }


}

object JavaAPI{

    private val javaScope = CoroutineScope(Dispatchers.Default)

    fun callFunction1(): CompletableFuture<Int> =
        javaScope.future {
            val result1= Bonus1JavaIntegration.function1()
            Bonus1JavaIntegration.function2(result1)
    }

}