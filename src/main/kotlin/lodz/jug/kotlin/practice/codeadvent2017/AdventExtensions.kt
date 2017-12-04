package lodz.jug.kotlin.practice.codeadvent2017

object AdventExtensions{
    fun Char.asDigit() = Integer.parseInt(this.toString())
}
