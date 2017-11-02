package lodz.jug.kotlin.starter.oop

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class StaticMethodsTest : StringSpec() {
    init {
        "Static Methods should add prefix " {
            val result = StaticMethods.prefix("prefix", "test")
            result shouldBe "prefix_test"
        }

        "Static Methods should add prefix " {
            val result = StaticMethods.multiply(4, 5)

            result shouldBe 20 //change to 19 and check result
        }
    }
}
