package lodz.jug.kotlin.starter.oop.answers

import io.kotlintest.matchers.containsAll
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.*

class StatefulInstanceInKotlinAnswer : StringSpec(){
    init{
        "Stateful class should correctly implement 'HasState' interface"{
           val instance:HasState=KotlinStatefulClass("one")

           instance.set("two")

           instance.get() shouldBe "two"
        }

        "Stateful class should correctly implement Data Change journal"{
            val instance=KotlinStatefulClass("one")

            instance.set("two")
            instance.set("three")
            instance.set("four")

            val history: Collection<Any> = instance.showHistory()
            history should containsAll<Any>("one","two","three","four") //notice that element from constructor is also here
        }
    }
}

interface HasState{
    fun set(state:Any)
    fun get():Any
}

class KotlinStatefulClass(private var state:Any) : HasState{

    private var journal: MutableList<Any> =java.util.LinkedList()

    init {
        journal.add(state)
    }

    override fun get(): Any = state

    override fun set(state: Any) {
        this.state=state
        journal.add(state)
    }

    fun showHistory(): Collection<Any> =Collections.unmodifiableCollection(journal)

}