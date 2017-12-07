package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.WordSpec
import java.util.*
import  lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.splitWhitespace

fun startRedistribution(registers: IntArray):Int{
    var distributionHistory= setOf<Int>()
    fun thereIsNoeRepeatition(it: IntArray): Boolean {
        return if (distributionHistory.contains(Arrays.hashCode(it))) false
        else {
            distributionHistory += Arrays.hashCode(it)
            true
        }
    }

    return generateSequence(registers,::redistribute)
            .takeWhile (::thereIsNoeRepeatition)
            .count()
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

        "part1" {
            val input="5\t1\t10\t0\t1\t7\t13\t14\t3\t12\t8\t10\t7\t12\t0\t6".splitWhitespace()
                    .map { it.toInt() }.toIntArray()
            println(startRedistribution(input))
        }
    }
}