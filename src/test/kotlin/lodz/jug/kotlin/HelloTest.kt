package lodz.jug.kotlin

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class HelloTest: StringSpec(){
    init {
        "strings.length should return size of string" {
            "hello".length shouldBe 5
        }

        "add 1 to 2 should be 3" {
            add(1,2) shouldBe 3
        }
    }
}