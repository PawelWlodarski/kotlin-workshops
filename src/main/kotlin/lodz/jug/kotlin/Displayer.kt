package lodz.jug.kotlin

object Displayer {

    fun header(text:String) = println("\n *** $text *** \n")
    fun title(s:String) = println(" *** $s ***")
    fun section(s:String) = println("    *** $s")
    fun <A> section(s:String,example:A) = println("    *** $s : $example")
    fun threadName() = Thread.currentThread().name
    fun displayThread() = println(threadName())
    fun displayThread(id:String) = println("[$id] ${threadName()}")
}