package lodz.jug.kotlin.starter.types.answers

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class GenericsAnswers : StringSpec(){
    init {
        "create GenericPair " {
            //My Pair code at the bottom
            val pair1:MyPair<String,Int> = MyPair("aaa",3)
            val pair2:MyPair<List<String>,List<Int>> = MyPair(listOf("aaa","bbb"),listOf(3,4))

            pair1.getA() shouldBe "aaa"
            pair1.getB() shouldBe 3
            pair2.getA() shouldBe listOf("aaa","bbb")
            pair2.getB() shouldBe listOf(3,4)
        }.config(enabled = false)

        "join two lists" {
             val l1: List<Int> =listOf(1,2,3,4,5)
             val l2: List<String> =listOf("aaa","bbb")
             val l3: List<Boolean> =listOf(true,false,true)

             ListJoiner.join(l1,l2) shouldBe listOf(1,2,3,4,5,"aaa","bbb")
             ListJoiner.join(l1,l3) shouldBe listOf(1,2,3,4,5,true,false,true)
             ListJoiner.join(l2,l3) shouldBe listOf("aaa","bbb",true,false,true)
        }

        //join with filtering
    }
}


class MyPair<A,B>(private val a:A,private val b:B){
    fun getA():A = a
    fun getB():B = b
}


object ListJoiner{
    fun <A:Any,B:Any> join(list1:List<A>,list2:List<B>): List<Any> = list1 + list2
    fun <A:Any,B:Any> joinWithFiltering(list1:List<A>,list2:List<B>,p:(A)->Boolean): List<Any> =
            list1.filter(p) + list2
}

//covariant generator