package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.splitWhitespace
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.readLines

sealed abstract class Node{
    abstract val name:String
}

data class Program(override val name:String, val children:List<ProgramName>) : Node()
data class ProgramName(override val name:String) : Node()

fun parseInput(file:String) = file.readLines().map(::parseLine)

fun searchBottom(input:List<Node>):String{
    val onlyParents=input.filter { it !is ProgramName }.map { it as Program }

    val allChildren=onlyParents.flatMap { it.children }.map { it.name }

    return onlyParents.find { !allChildren.contains(it.name) }!!.name
}

private fun String.asProgramName()=ProgramName(this)
private fun parseLine(line:String) = if("->" in line) parseNode(line) else parseLeaf(line)


private fun parseLeaf(line:String): ProgramName =  line.splitWhitespace()[0].asProgramName()
private fun parseNode(line:String) : Program {
    val elements=line.split("->")
    val name=elements[0].splitWhitespace()[0]
    val children= elements[1].split(",").map { it.trim().asProgramName() }
    return Program(name,children)
}


class Day7Tests:WordSpec(){
    init {
        "split leaf line" {
            parseLeaf("uwzmqi (57)") shouldBe "uwzmqi".asProgramName()
        }

        "split node line" {
            parseNode("mjftixu (131) -> kwzsj, rspilzk, amtxw")shouldBe
                    Program("mjftixu", listOf("kwzsj".asProgramName(),"rspilzk".asProgramName(), "amtxw".asProgramName()))
        }

        "find bottom in test data"{
            val nodes=parseInput("files/day7_test.txt")

            searchBottom(nodes) shouldBe "tknk"
        }

        "part 1"{
            val nodes=parseInput("files/day7.txt")

            println(searchBottom(nodes))
        }
    }
}