package lodz.jug.kotlin.reactive.croutines

fun displayThread(message: String) = println("thread : ${Thread.currentThread().name} : $message")


fun withTimeMeasurement(title:String,isActive:Boolean=true,code:() -> Unit){
    if(!isActive) return

    val timeStart=System.currentTimeMillis()
    displayThread("start code")
    code()
    displayThread("end code")
    val timeEnd=System.currentTimeMillis()


    println("operation in '$title' took ${(timeEnd- timeStart)} ms")
}


