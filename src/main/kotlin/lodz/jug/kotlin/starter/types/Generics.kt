package lodz.jug.kotlin.starter.types

import lodz.jug.kotlin.Displayer


fun main(args: Array<String>) {
    Displayer.header("GENERICS")

    //*********** WHEN WE NEED GENERICS *****************
    Displayer.title("when 'any' is not enough")

    val anyList:MutableList<Any> = mutableListOf() //java 1.4 emulation
    anyList += "aaa"  //show plus assign
    anyList += false
    anyList += "ccc"

    val anyFirst = anyList[1] as? String

    val specificList:MutableList<String> = mutableListOf()
    specificList += "aaa"
//    specificList += false  //show that this is 'plus' not plus assign
//    specificList.add(false)  //type error

    specificList += "bbb"


    //or even that way
//  val specificList2:MutableList<String> = mutableListOf("aaa","bbbb",1)

    //*********** GENERIC OPERATORS *****************
    Displayer.title("specific type operators")
    class Wrapper<A>(val a:A){
        fun <B> modify(f:(A)->B):Wrapper<B> = Wrapper(f(a))

        override fun toString(): String {
            return "Wrapper(a=$a)"
        }
    }

    val stringWrapper:Wrapper<String> = Wrapper("initial")
    val intWrapper: Wrapper<Int> =stringWrapper.modify { it.length }
    val booleanWrapper: Wrapper<Boolean> = stringWrapper.modify { it.startsWith("ini") }
    val otherStringWrapper: Wrapper<String> = stringWrapper.modify { it.toUpperCase() }

    Displayer.section("IntWrapper",intWrapper)
    Displayer.section("booleanWrapper",booleanWrapper)
    Displayer.section("otherStringWrapper",otherStringWrapper)

    Displayer.title("more advanced operators")
    fun <A,B> zip(wrapperA:Wrapper<A>, wrapperB:Wrapper<B>):Wrapper<Pair<A,B>> = Wrapper(Pair(wrapperA.a, wrapperB.a))

    val zipped: Wrapper<Pair<String, Int>> = zip(otherStringWrapper,intWrapper)
    Displayer.section("zippedWrapper",zipped)

    //*********** TYPE BOUNDS *****************
    Displayer.title("type bounds and null safety")

    val i:Int? = null
    val s:String? = null
    val s2:String? = "aaa"
    val s3:String = "aaa"

//    fun <T> naiveToString(t:T) :String = t.toString()  //explain why it is working
    fun <T> naiveHash(t:T) :Int = t!!.hashCode()

//    println(naiveHash(i)) nullpointer
//    naiveHash(s)
//    naiveHash(s2)
    Displayer.section("naiveHash(s3)",naiveHash(s3))

    fun <T : Any> safeHash(t:T) = t.hashCode() //no !!
//    safeHash(s) //compilation error!
    Displayer.section("safeHash(s3)",safeHash(s3))

    //*********** TYPE ERASURE AND REIFIED TYPES *****************
    Displayer.title("generics erasure & reified types")
//    fun <A> afterErasure(a:Any):Unit = when(a){
//        is A -> println("a is of expected type")  //error cannot check of instance of erased ...
//        else -> println("a is of different type")
//    }

    //NOW LOOK AT THE BOTTOM OF THE FILE  --> Tools/Kotlin/Decompile !!!!!
    Displayer.section("reified expected boolean", reifiedType<Boolean>(true))
    Displayer.section("reified unexpected int", reifiedType<Boolean>(3))

    //*********** VARIANCE *****************
    Displayer.title("Variance")

    //* - show Java invariance

    Displayer.section("covariant kotlin list")
    val strings:List<String> = listOf("aaa","bbb")  //SHOW LIST CONSTRUCTOR
    val anys:List<Any> = strings
    val elem1: Any =anys[0]

    Displayer.section("access to elements",elem1)
    val commonType: List<Any> =strings + 1 // String + Int => Any

    val commonTypeNumbers:List<Number> = listOf<Int>(1,2) + 2.0

    val firstNumber: Number =commonTypeNumbers[1]

    Displayer.section("first number",firstNumber)

    //CONTRAVARIANCE - LEFT FOR NEXT MODULE

    //**********ARRAYS***********/
    Displayer.title("Arrays Invariance")
    val arrStrings: Array<String> = arrayOf("aaa","bbb")
//    val arrAnys: Array<Any> = arrStrings  FORBIDDEN


}

inline fun <reified A> reifiedType(a: Any): String = when (a) {
    is A -> "a is of expected type '${a::class}' (${a.javaClass})"  //error cannot check of instance of erased ...
    else -> "a is of different type '${a::class}' where '${A::class}' was expected"
}
