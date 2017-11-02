package lodz.jug.kotlin.starter.oop.answers

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import lodz.jug.kotlin.starter.oop.JavaStatefulObject

class TestingJavaClassesAnswer : StringSpec(){
    init {
        "state in java class should be set correctly" {
            val sut= JavaStatefulObject()

            sut.state=101 //???

            sut.state shouldBe 101
        }

        "convert to string correctly" {
            val sut= JavaStatefulObject() //???

            sut.state="STATE"

            sut.toString() shouldBe "JavaStatefulObject{state=STATE}"
        }
    }
}