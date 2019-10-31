package lodz.jug.kotlin.coroutines

object ExerciseLogger {
    fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
}