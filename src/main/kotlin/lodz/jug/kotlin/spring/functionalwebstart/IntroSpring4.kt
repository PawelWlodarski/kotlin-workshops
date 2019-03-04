package lodz.jug.kotlin.spring.functionalwebstart

import lodz.jug.kotlin.Displayer
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

fun main() {
    Displayer.header("Pure Configuration")

    Displayer.title("3 beans")
    class Bean1(val info: String = "Bean1")
    class Bean2(val info: String = "Bean2")
    class Bean3(val b1: Bean1, val b2: Bean2) {
        override fun toString(): String {
            return "Bean3(b1=${b1.info}, b2=${b2.info})"
        }
    }

    val c = IntroSpring4.Context()

    Displayer.section("adding bean 1 and bean 2")
    c.addBean<Bean1>()
    c.addBean<Bean2>()

    Displayer.section("adding bean 3")
    c.addBean {
        val b1 = ref<Bean1>()
        val b2 = ref<Bean2>()

        //SMART context
        if (b1 != null && b2 != null) Bean3(b1, b2)
        else throw RuntimeException("incorrect configuration")

    }


    val retrievedB3 = c.ref(Bean3::class)
    Displayer.section("retrieved bean 3 has value", retrievedB3)
}

object IntroSpring4 {


    class Context {
        //DIFFERENT CLASSES
        private var beans: Map<KClass<*>, Any> = emptyMap()
        private var javaBeans: Map<Class<*>, Any> = emptyMap()

        //SHOW problem with private access
        inline fun <reified T : Any> addBean() = addBean(T::class)

        fun <T : Any> addBean(c: KClass<T>) {
            beans = beans + (c to c.createInstance())
        }

        fun <T : Any> addBean(customizer: Context.() -> T) {
            val bean: T = customizer(this)
            beans = beans + (bean::class to bean)
        }


        //EXPLAIN WHY 'T : ANY'
        inline fun <reified T : Any> addJavaBean(): Unit = addJavaBean(T::class.java)

        fun <T : Any> addJavaBean(c: Class<T>) {
            javaBeans = javaBeans + (c to c.newInstance())
        }


        //EXPLAIN WHY NULLABLE TYPE
        inline fun <reified T : Any> ref(): T? = ref(T::class)

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> ref(c: KClass<T>): T? = beans[c] as T?
    }
}


