package lodz.jug.kotlin.starter.oop.exercises

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import lodz.jug.kotlin.starter.oop.JavaStatefulObject

class TestingJavaClassesExercise : StringSpec(){
    init {
        "state in java class should be set correctly" {
            val sut= JavaStatefulObject()

            //???

            sut.state shouldBe 101
        }

        //UNCOMMENT AND FIX
//        "convert to string correctly" {
//            //???
//
//            //???
//
//            sut.toString() shouldBe "JavaStatefulObject{state=STATE}"
//        }
    }
}