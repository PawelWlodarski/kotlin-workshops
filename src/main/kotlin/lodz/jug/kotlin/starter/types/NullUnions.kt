package lodz.jug.kotlin.starter.types

import lodz.jug.kotlin.Displayer

fun main(args: Array<String>) {
    Displayer.header(" KOTLIN STARTER TYPES - NULL UNIONS")

    val sOrNull: String? = "aaa"
    val sOrNull2: String? = null

//    println(s2OrNull.toUpperCase()) FORBIDDEN - EXPLAIN

    if (sOrNull != null) println(sOrNull.toUpperCase()) // why now it is legal?

    Displayer.title("TYPESAFE NULLs IN METHODS") //type? = type or null

    fun safeUpper(input: String?): String {
        when (input) {  //explain warning and show code simplification
            null -> return ""
            else -> return input.toUpperCase()
        }
    }

    Displayer.section("safe upper non null", safeUpper(sOrNull))
    Displayer.section("safe upper null", safeUpper(sOrNull2))

    fun naiveUpper(input: String) = input.toUpperCase()
//    unsafeUpper(sOrNull) //compilation error
//    val s:String=sOrNull //compilation error
//    val s:String=null  //compilation error

    Displayer.title("NULL UNION TRAVERSAL")

    val unionResult1: String? = sOrNull?.toUpperCase()
    val unionResult2: String? = sOrNull?.toUpperCase()?.trim()?.decapitalize()
    val unionResult3: String? = sOrNull2?.toUpperCase()?.trim()?.decapitalize()

    Displayer.section("union result 1", unionResult1)
    Displayer.section("union result 2", unionResult2)
    Displayer.section("union result 3", unionResult3)

    Displayer.title("NULL UNION FOLDING (Elvis)")

    val foldResult1: String =sOrNull?.toUpperCase() ?: ""
    val foldResult2: String =sOrNull2?.toUpperCase() ?: ""

    Displayer.section("fold result 1", foldResult1)
    Displayer.section("fold result 2", foldResult2)

    Displayer.section("nuclear option",naiveUpper(sOrNull!!))
//    Displayer.section("nuclear option",naiveUpper(sOrNull2!!)) // Nullpointer exception


    Displayer.title("CASTING TO NULL UNION")

    val o1:Any=1
    val o2:Any="bbb"


    val casted1: Int? =o1 as? Int
    val casted2: Int =o1 as Int
    val casted3: Int? =o2 as? Int


    Displayer.section("casted1", casted1)
    Displayer.section("casted2", casted2)
    Displayer.section("casted3", casted3)


    Displayer.title("UNION MODELING")

    val value: NullUnion = NullUnion.of("someText")
    val nullValue: NullUnion = NullUnion.of(null)

    Displayer.section("value",value)
    Displayer.section("nullValue",nullValue)
}

sealed abstract class NullUnion{
    abstract fun safeCall(call:(String) -> String): NullUnion

    companion object {
        fun of(input:String?):NullUnion = if(input==null) NullValue else NonNullValue(input)
    }
}

class NonNullValue (private val v:String) : NullUnion(){
    override fun safeCall(call:(String) -> String): NullUnion = NonNullValue(call(v))
    override fun toString(): String {
        return "NonNullValue(v='$v')"
    }

}

object NullValue : NullUnion(){
    override fun safeCall(call: (String) -> String): NullUnion  = this
    override fun toString(): String ="NullValue"

}