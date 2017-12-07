package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.WordSpec
import kategory.Option
import kategory.Option.None
import kategory.Option.Some
import kategory.getOrElse
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.splitWhitespace
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.valueHash

class DistributionState{
    private var distributionHistory= setOf<Int>()
    private var repeated:Option<Int> = None

    fun thereIsNoRepeatition(it: IntArray): Boolean {
        return if (distributionHistory.contains(it.valueHash())) {
            repeated=Some(it.valueHash())
            false
        }
        else {
            distributionHistory += it.valueHash()
            true
        }
    }

    val repeatedElement:Int
            get()=  repeated.getOrElse { throw RuntimeException("it's only an exercise") }
}

fun startRedistribution(registers: IntArray):Int{
    val state=DistributionState()

    return generateSequence(registers,::redistribute)
            .takeWhile {registers->state.thereIsNoRepeatition(registers)}
            .count()
}


fun countCycles(registers: IntArray):Int{
    val state=DistributionState()

    val fromBeginignToCycle=generateSequence(registers,::redistribute)
            .takeWhile {registers->state.thereIsNoRepeatition(registers)}.toList()

    return fromBeginignToCycle.map{it.valueHash()}.dropWhile { it != state.repeatedElement}.count()
}




fun redistribute(registers: IntArray): IntArray {
    val newRegisters = registers.clone()
    val max = newRegisters.max()!!
    val indexOfMax = newRegisters.indexOf(max)
    newRegisters[indexOfMax] = 0
    val registersIndex=registers.size

    generateSequence((indexOfMax + 1)%registersIndex) { (it + 1) % registersIndex }
            .take(max)
            .forEach { newRegisters[it] = newRegisters[it].inc() }

    return newRegisters
}


class RedistributionTest : WordSpec() {
    init {
        "single redistribution is correct" {
            val data = table(
                    headers("initialState", "resultState"),
                    row(intArrayOf(0, 2, 7, 0), intArrayOf(2, 4, 1, 2)),
                    row(intArrayOf(2, 4, 1, 2), intArrayOf(3, 1, 2, 3)),
                    row(intArrayOf(3, 1, 2, 3), intArrayOf(0, 2, 3, 4)),
                    row(intArrayOf(0, 2, 3, 4), intArrayOf(1, 3, 4, 1)),
                    row(intArrayOf(1, 3, 4, 1), intArrayOf(2, 4, 1, 2))
            )

            forAll(data) { input, output ->
                redistribute(input) shouldBe output
            }
        }

        "full redistribution has proper number of steps"{
            val input=intArrayOf(0, 2, 7, 0)

            startRedistribution(input) shouldBe 5
        }

        "find cycle length"{
            val input=intArrayOf(0, 2, 7, 0)

            countCycles(input) shouldBe 4
        }

        "part1" {
            val input="5\t1\t10\t0\t1\t7\t13\t14\t3\t12\t8\t10\t7\t12\t0\t6".splitWhitespace()
                    .map { it.toInt() }.toIntArray()
            println(startRedistribution(input))
        }

        "part2" {
            val input="5\t1\t10\t0\t1\t7\t13\t14\t3\t12\t8\t10\t7\t12\t0\t6".splitWhitespace()
                    .map { it.toInt() }.toIntArray()
            println(countCycles(input))
        }
    }
}