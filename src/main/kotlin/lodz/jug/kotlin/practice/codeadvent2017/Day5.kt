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




private fun runProcedure(updateStrategy:(Int) -> Int): (IntArray) -> Int = {procedure ->
    fun nextPosition(current: Int) = (current + procedure[current])
    fun updatePosition(position: Int) {
        procedure[position] = updateStrategy(procedure[position])
    }

    tailrec fun procedureStep(position: Int, steps: Int): Int = when {
        nextPosition(position) >= procedure.size -> steps
        else -> {
            val toJump = nextPosition(position)
            updatePosition(position)
            procedureStep(toJump, steps + 1)
        }

    }

    procedureStep(0, 1)
}

private val incrementalUpdate = { i:Int -> i+1}
private val stabilizingUpdate: (Int) -> Int= {if (it>=3) it-1 else it+1}

class TestJumpProcedure : WordSpec() {
    init {
        "should properly perform procedure for step1"{
            val input = intArrayOf(0, 3, 0, 1, -3)
            runProcedure(incrementalUpdate)(input) shouldBe 5
        }

        "should properly perform procedure for step 2"{
            val input = intArrayOf(0, 3, 0, 1, -3)
            runProcedure(stabilizingUpdate)(input) shouldBe 10
        }

        "part 1" {
            val program=::parseSteps andThen runProcedure(incrementalUpdate)

            println(program())
        }

        "part 2" {
            val program=::parseSteps andThen runProcedure(stabilizingUpdate)

            println(program())
        }
    }
}

