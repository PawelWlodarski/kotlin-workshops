package lodz.jug.kotlin.spring.functionalwebstart.answers

import arrow.core.*
import io.kotlintest.fail
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Test

class IntroSpring4Answers {

    class Bean1(val info:String="Bean1"){  //WHAT HAPPEN WITHOUT DEFAULT VALUE ?
        override fun toString(): String {
            return "Bean1(info='$info')"
        }
    }
    class Bean2(private val info:String="Bean2"){
        override fun toString(): String {
            return "Bean2(info='$info')"
        }
    }
    class Bean3(private val b1:Bean1, private val b2:Bean2){
        override fun toString(): String {
            return "Bean3(b1=$b1, b2=$b2)"
        }
    }

    @Test
    fun shouldAddNamedBeansToConfig(){
            val ctx = NamedContext()
            ctx.register<Bean1>()
            ctx.register<Bean1>()
            ctx.register<Bean1>("namedBean1")

            ctx.register<Bean2>()
            ctx.register<Bean2>("namedBean2")

            ctx.register("b3"){
                val b1=findNamed<Bean1>("namedBean1")
                val b2 =findNamed<Bean2>("namedBean2")

                val b3: Bean3? =b1?.let { bean1 ->
                    b2?.let { bean2 ->
                        Bean3(bean1,bean2)
                    }
                }

                b3 ?: throw RuntimeException("incorrect configuration")
            }

            val foundB3=ctx.findNamed<Bean3>("b3")
            println(foundB3)

    }

    @Test
    fun shouldUseOptionalAPI(){
        val ctx = NamedContext()
        ctx.findOptional<Bean1>() shouldBe None

        ctx.register<Bean1>()
        ctx.register<Bean1>()
        ctx.register<Bean2>()
        when(val candidate1=ctx.findOptional<Bean1>()){
            is None -> fail("bean1 should be some")
            is Some -> candidate1.t.info shouldBe "Bean1"
        }


        ctx.register{
            val b1Candidate: Option<Bean1> =findOptional()  //NO Generics here!!
            val b2Candidate: Option<Bean2> =findOptional()

            val b3candidate: Option<Bean3> =b1Candidate.flatMap { b1 ->   // EXPLAIN FOR-COMPREHENSION IDEA
                b2Candidate.map {b2 ->
                    Bean3(b1,b2)
                }
            }

            b3candidate.getOrElse { throw java.lang.RuntimeException("wrong configuration") }
        }


        ctx.findOptional<Bean3>() shouldBeInstanceOf Some::class.java
    }


    class NamedContext{
        data class BeanContext<T>(val name:String,val instance:T)


        private var beans:List<BeanContext<*>> = emptyList()

        inline fun <reified T:Any> register(name:String? = null){
            register(T::class.java,name)
        }

        fun <T : Any> register(c:Class<T>,name:String? = null) {
            val finalName = name ?: c.simpleName
            beans= beans + BeanContext(finalName, c.newInstance())
        }

        fun <T : Any> register(name:String? = null, init : NamedContext.() -> T){
            val instance = init(this)
            val finalName = name ?: instance.javaClass.simpleName
            beans= beans + BeanContext(finalName, instance)
        }


        @Suppress("UNCHECKED_CAST")
        fun <T : Any> findNamed(name:String):T? = beans.find {
            it.name == name
        }?.instance as? T

        inline fun <reified T : Any> findOptional():Option<T> = findOptional(T::class.java)

        fun <T : Any> findOptional(c:Class<T>):Option<T> = beans.find {
            (it.instance as Any).javaClass  == c
        }.toOption().map { it.instance as T }
    }


}