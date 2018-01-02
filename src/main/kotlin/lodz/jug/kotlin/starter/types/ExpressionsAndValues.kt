package lodz.jug.kotlin.starter.types

import lodz.jug.kotlin.Displayer

//http://natpryce.com/articles/000818.html
fun main(args: Array<String>) {
    Displayer.header("VALUES IN TYPE SYSTEM")


    Displayer.title("Everything is a type")
    val cond=Math.random() > 0.5
    val res: Int =if(cond) 1 else 2

    val res2: Int =when(cond){
        true -> 1
        false -> 2
    }

    Displayer.section("values from 'if' $res, from 'when' $res2")

    val l1:List<Any> = listOf(1,false,1.2, listOf(1,2,3))
    Displayer.section("top of the type system",l1)
//BOTOM - UNCOMMENT
//    fun bottom():Nothing = throw RuntimeException()
//    val i1:Int=bottom() // compiles but exception at runtime
//    val s1:String= bottom()

//    val i2 = if (cond) 1 else bottom()
//    Displayer.section("bottom of the type system",bottom())


    Displayer.section("Unit as empty set",Unit)

    fun myprint(s:String):Unit = println(s)
//    fun myprint2(s:String):Nothing = println(s) //EXPLAIN WHY UNIT CAN NOT BE ASSIGNED TO NOTHING
    fun myprint3(s:String):Any = println(s)

    Displayer.section("Only one empty set",Unit == Unit)


    Displayer.title("Smart casts")

    val any:Any="aaa"

    val asint: String =any as String
//    val asintExplode: Int? ="Aaa" as Int
    val asintSafe: String? =any as? String

    Displayer.section("asIntSafe",asintSafe?.toUpperCase())


    if(any is String){
        Displayer.section("intelligent compiler",any.toUpperCase())
    }


    Displayer.title("Extending nullable types")


    Displayer.section("nonNull length","nonNull".safeLength())
    Displayer.section("Null length",null.safeLength())


    Displayer.title("Primitives")
    val i:Int =  1
//    val j:Long =  i    /error
    val j2:Long=i.toLong()
    i > 3 //show implementation
}

fun String?.safeLength() = this?.length ?: 0