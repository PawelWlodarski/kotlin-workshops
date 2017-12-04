package lodz.jug.kotlin.practice.codeadvent2017

private fun steps(from: Int):Int {
    val level = Math.ceil((Math.sqrt(from.toDouble()) - 1) / 2).toInt()
    val br = Math.pow((level * 2 + 1).toDouble(), 2.0).toInt()
    val offset = br - from
    val length = level * 2 + 1
    val k = length - 1


    val (x, y) = when {
        offset < k -> Pair(k / 2 - offset, -level)
        offset < k * 2 -> Pair(-level, offset - k / 2 - k)
        offset < k * 3 -> Pair(offset - k / 2 - 2 * k, level)
        else -> Pair(level, 2 * k - offset + level * 3)
    }

    return Math.abs(x) + Math.abs(y)
}

fun main(args: Array<String>) {
    println(steps(277678))
}