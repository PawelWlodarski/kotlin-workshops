package lodz.jug.kotlin.starter.types.answers

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.*

class GenericsAnswers : StringSpec(){
    init {
        "create GenericPair ".config(enabled = false) { //EXERCISE1
            //My Pair code at the bottom
            val pair1:MyPair<String,Int> = MyPair("aaa",3)
            val pair2:MyPair<List<String>,List<Int>> = MyPair(listOf("aaa","bbb"),listOf(3,4))

            pair1.getA() shouldBe "aaa"
            pair1.getB() shouldBe 3
            pair2.getA() shouldBe listOf("aaa","bbb")
            pair2.getB() shouldBe listOf(3,4)
        }

        "join two lists" { //EXERCISE2
             val l1: List<Int> =listOf(1,2,3,4,5)
             val l2: List<String> =listOf("aaa","bbb")
             val l3: List<Boolean> =listOf(true,false,true)

             ListJoiner.join(l1,l2) shouldBe listOf(1,2,3,4,5,"aaa","bbb")
             ListJoiner.join(l1,l3) shouldBe listOf(1,2,3,4,5,true,false,true)
             ListJoiner.join(l2,l3) shouldBe listOf("aaa","bbb",true,false,true)
        }

        "join two lists with filtering" {
            val l1: List<Int> =listOf(1,2,3,4,5)
            val l2: List<String> =listOf("aa","bbbbb","c")
            val l3: List<Boolean> =listOf(true,false,true)

            ListJoiner.joinWithFiltering(l1,l2,{it > 3}) shouldBe listOf(4,5,"aa","bbbbb","c")
            ListJoiner.joinWithFiltering(l1,l3,{it == 2}) shouldBe listOf(2,true,false,true)
            ListJoiner.joinWithFiltering(l2,l3,{it.length <= 2 }) shouldBe listOf("aa","c",true,false,true)
        }



        "create any instance through reflection" { // EXERCISE3
            val s:String=AnyTypeFactory.create()
            val d:Date=AnyTypeFactory.create()
            val p:MyPair<String,Int> = AnyTypeFactory.createWithArgs("aaa",2)
            val p2:MyPair<Boolean,Double> = AnyTypeFactory.createWithArgs(false,3.0)

            s should beInstanceOf(String::class)
            d should beInstanceOf(Date::class)
            p should beInstanceOf(MyPair::class)
            p2 should beInstanceOf(MyPair::class)
        }

        "Functional composition" { //EXERCISE4
            val f1:(String) -> Int = {it.length}
            val f2:(Int) -> Boolean = {it>3}
            val f3:(Int) -> Int = {it * 2}

            val isStringLong=FuncLibrary.andThen(f1,f2)
            val doubleStringLength=FuncLibrary.andThen(f1,f3)

            isStringLong("aa") shouldBe false
            isStringLong("aaa") shouldBe false
            isStringLong("aaaa") shouldBe true
            isStringLong("aaaaaa") shouldBe true

            doubleStringLength("aa") shouldBe 4
            doubleStringLength("aaa") shouldBe 6
            doubleStringLength("aaaa") shouldBe 8
        }

        "currying" {
            val f1:(Int,Int) -> Boolean = {i1,i2 -> i1>i2}
            val f2:(String,String) -> String = {s1,s2 -> s1+s2}

            val f1Curried: (Int) -> (Int) -> Boolean =FuncLibrary.curry(f1)
            val f2Curried: (String) -> (String) -> String =FuncLibrary.curry(f2)

            val isSmallerThanFive: (Int) -> Boolean =f1Curried(5)
            val isSmallerThanThree: (Int) -> Boolean =f1Curried(3)

            isSmallerThanFive(3) shouldBe true
            isSmallerThanFive(6) shouldBe false
            isSmallerThanThree(3) shouldBe false


            val prompt: (String) -> String =f2Curried("c:>")
            prompt("dir") shouldBe "c:>dir"
            prompt("format c") shouldBe "c:>format c"


            FuncLibrary.uncurry(f2Curried)("a:>","dir") shouldBe "a:>dir"
            FuncLibrary.uncurry(f1Curried)(7,5) shouldBe true
        }

    }
}

//EXERCISE1
class MyPair<A,B>(private val a:A,private val b:B){
    fun getA():A = a
    fun getB():B = b
}

//EXERCISE2
object ListJoiner{
    fun <A:Any,B:Any> join(list1:List<A>,list2:List<B>): List<Any> = list1 + list2
    fun <A:Any,B:Any> joinWithFiltering(list1:List<A>,list2:List<B>,p:(A)->Boolean): List<Any> =
            list1.filter(p) + list2
}

//EXERCISE3
object AnyTypeFactory {
    inline fun <reified T> create():T = T::class.constructors.first { it.parameters.isEmpty() }.call()
    inline fun <A,B,reified T> createWithArgs(a:A,b:B):T = T::class.constructors.first{ it.parameters.size == 2 }.call(a,b)
}

//EXERCISE4
object FuncLibrary{
    fun <A,B,C> andThen(f:(A)->B,g:(B)->C):(A) -> C = {a -> g(f(a))}
    fun <A,B,C> curry(f:(A,B)->C):(A) -> (B) -> C = {a -> {b -> f(a,b)}}
    fun <A,B,C> uncurry(f:(A) -> (B)->C):(A,B) -> C = {a,b -> f(a)(b)}
}