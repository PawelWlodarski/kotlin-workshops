package lodz.jug.kotlin.spring.functionalwebstart.scratches


fun main() {
    usingJavaFunctionalInterface()
}

fun usingJavaFunctionalInterface(){
    val kotlinImplementation=SomeFunctionalInterface { input ->
        input.length
    }

    println("length is : " + kotlinImplementation.doSomething("someText"))
}