package lodz.jug.kotlin.starter.fp

import lodz.jug.kotlin.Displayer
import arrow.core.andThen

fun main(args: Array<String>) {
    Displayer.header(" KOTLIN STARTER FP - Functions")


    val f: (Int)->Int = {input -> input+1} //brackets in type very important
    val f2: (Int)->Int = {input -> input+1} //brackets in type very important
    val add: (Int,Int)->Int = {a1,a2 -> a1+a2} //brackets in type very important

    Displayer.section("function class",f.javaClass)
    Displayer.section("function class",f2.javaClass)
    Displayer.section("function class",add.javaClass)

    Displayer.section("add(2,3)",add(2,3))
    //kotlin.jvm.functions.   // search for Function1

    Displayer.title("placeholders")

    val f3 : (Int) -> Int = {it +1}
    Displayer.section("add with placeholder",f3(2))


    Displayer.title("Composition")
    val parse: (String) -> Int = {s -> s.toInt()} //exercise - make short with underscore
    val square: (Int) -> Int = {i -> i * i}

    //ZONK - no composition in standard lib
    //import org.funktionale.composition.*

    val composed=parse andThen square  //show and then definition //better modularisation???
    Displayer.section("composedFunction: square(parsed('2'))",composed("2"))

    Displayer.title("function customization")
    fun <A,B> genericInvoke(a:A,f:(A)->B):B = f(a)
    fun <A,B> ((A)->B).customInvoke(a:A)= this(a)
    fun createIncr() : (Int) -> Int = { it + 1}

    val incr: (Int) -> Int =createIncr()
    Displayer.section("generic invocation",genericInvoke(3,incr))
    Displayer.section("extended function invocation",incr.customInvoke(3))

    Displayer.title("TUPLES")

    val pair=Pair(1,"a")
    val triple=Triple(1,"a",false)

    val (t1,t2) = pair
    val (number,string,bool) = triple


    Displayer.section("(t1,t2)","($t1,$t2)")
    Displayer.section("(number,string,bool)","($number,$string, $bool)")

}