package lodz.jug.kotlin.practice.codeadvent2017

import java.io.File
import java.util.*

object AdventExtensions{
    fun Char.asDigit() = Integer.parseInt(this.toString())
    fun String.splitWhitespace() = this.split("\\s+".toRegex())
    fun String.lines()= this.split("\n")
    fun String.readLines()= File(this).readLines()

    fun Int.pow2():Int = Math.pow(this.toDouble(),2.0).toInt()

    fun IntArray.indexOfMax()= this.indexOf(this.max()!!)
    fun IntArray.valueHash()= Arrays.hashCode(this)
}
