package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec
import org.funktionale.composition.andThen
import java.io.File

private fun parseSteps(): IntArray =
    File("files/day5.txt")
            .readLines()
            .map(Integer::parseInt)
            .toIntArray()




private fun runProcedure(procedure: IntArray): Int {
    fun nextPosition(current: Int) = (current + procedure[current])
    fun updatePosition(position: Int) {
        procedure[position] = procedure[position] + 1
    }

    tailrec fun procedureStep(position: Int, steps: Int): Int = when {
        nextPosition(position) >= procedure.size -> steps
        else -> {
            val toJump = nextPosition(position)
            updatePosition(position)
            procedureStep(toJump, steps + 1)
        }

    }

    return procedureStep(0, 1)
}

class TestJumpProcedure : WordSpec() {
    init {
        "should properly perform procedure"{
            val input = intArrayOf(0, 3, 0, 1, -3)
            runProcedure(input) shouldBe 5
        }

        "part 1" {
            val program=::parseSteps andThen ::runProcedure

            println(program())
        }
    }
}

