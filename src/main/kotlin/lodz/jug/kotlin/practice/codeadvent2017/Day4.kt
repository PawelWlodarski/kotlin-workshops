package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.splitWhitespace
import lodz.jug.kotlin.practice.codeadvent2017.AdventExtensions.pow2
import java.io.File

private fun checkPhrase(input: String): Boolean {
    val words = input.splitWhitespace()
    return words.size == words.toSet().size
}

private fun checkAnagramPhrase(input:String) : Boolean {
    fun encodeWord(s:String):Int = s.map { it.toInt().pow2() }.sum()
    val words=input.splitWhitespace()
    return words
            .map (::encodeWord)
            .distinct()
            .size == words.size
}


private fun countPhrases(phraseChecker:(String)->Boolean) : (List<String>) -> Int ={lines ->
    lines.filter(phraseChecker).count()
}

class PhraseTest : WordSpec() {
    init {
        "test phrases" {
            checkPhrase("aa bb cc dd ee") shouldBe true
            checkPhrase("aa bb cc dd aa") shouldBe false
            checkPhrase("aa bb cc dd aaa") shouldBe true
        }

        "test anagrams" {
            checkAnagramPhrase("abcde fghij") shouldBe true
            checkAnagramPhrase("abcde xyz ecdab") shouldBe false
            checkAnagramPhrase("a ab abc abd abf abj") shouldBe true
            checkAnagramPhrase("iiii oiii ooii oooi oooo") shouldBe true
            checkAnagramPhrase("oiii ioii iioi iiio") shouldBe false
        }

        "phrases part 1" {
            val input = File("files/day4.txt").readLines()
            println(countPhrases(::checkPhrase)(input))
        }

        "phrases part 3" {
            val input = File("files/day4.txt").readLines()
            println(countPhrases(::checkAnagramPhrase)(input))
        }
    }

}