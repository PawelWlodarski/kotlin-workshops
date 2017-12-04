package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.splitWhitespace
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.lines
import java.io.File

private fun checkPhrase(input: String): Boolean {
    val words = input.splitWhitespace()
    return words.size == words.toSet().size
}


private fun countPhrases(lines: List<String>) = lines.filter(::checkPhrase).count()

class PhraseTest : WordSpec() {
    init {
        "test phrases" {
            checkPhrase("aa bb cc dd ee") shouldBe true
            checkPhrase("aa bb cc dd aa") shouldBe false
            checkPhrase("aa bb cc dd aaa") shouldBe true
        }

        "phrases part 1" {
            val input = File("files/day4.txt").readLines()
            println(countPhrases(input))
        }
    }

}