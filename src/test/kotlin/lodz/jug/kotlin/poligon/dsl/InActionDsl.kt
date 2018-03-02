package lodz.jug.kotlin.poligon.dsl

fun buildString(builderAction : StringBuilder.() -> Unit):String {
    val sb=StringBuilder()
    sb.builderAction()
    return sb.toString()
}

fun main(args: Array<String>) {
    val r=buildString {
        append("Hello, ")
        append("World! ")
    }

    println(r)
}