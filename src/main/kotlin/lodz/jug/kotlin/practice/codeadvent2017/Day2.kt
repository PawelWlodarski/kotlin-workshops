package lodz.jug.kotlin.practice.codeadvent2017

import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.WordSpec
import kategory.andThen


private fun checksum(input:String):Int=
    input.split("\n")
                .map(lineProgram)
                .sum()


private fun splitOnWhitespaces(s:String) : List<String> = s.split("\\s+".toRegex())
private fun toNumbers(l:List<String>) : List<Int> = l.map{it.toInt()}
private val unsafeDifference: (List<Int>) -> Int = { it.max()!! - it.min()!! }

private val lineProgram = ::splitOnWhitespaces andThen ::toNumbers andThen unsafeDifference

class Day2Tests : WordSpec(){
    init {
        "line program calculates difference for single line"{
            val lines=table(
                    headers("line","checksum"),
                    row("5 1 9 5",8),
                    row("7 5 3",4),
                    row("2 4 6 8",6)
            )

            forAll(lines){line,result ->
                lineProgram(line) shouldBe result
            }
        }

        "properly count checksum of matrix"{
            val input="5 1 9 5\n" +
                    "7 5 3\n" +
                    "2 4 6 8"

            checksum(input) shouldBe 18
        }

        "my checksum" {
            val input="157\t564\t120\t495\t194\t520\t510\t618\t244\t443\t471\t473\t612\t149\t506\t138\n" +
                    "1469\t670\t47\t604\t1500\t238\t1304\t1426\t54\t749\t1218\t1409\t60\t51\t1436\t598\n" +
                    "578\t184\t2760\t3057\t994\t167\t2149\t191\t2913\t2404\t213\t1025\t1815\t588\t2421\t3138\n" +
                    "935\t850\t726\t155\t178\t170\t275\t791\t1028\t75\t781\t138\t176\t621\t773\t688\n" +
                    "212\t977\t297\t645\t229\t194\t207\t640\t804\t509\t833\t726\t197\t825\t242\t743\n" +
                    "131\t43\t324\t319\t64\t376\t231\t146\t382\t162\t464\t314\t178\t353\t123\t446\n" +
                    "551\t121\t127\t155\t1197\t288\t1412\t1285\t557\t137\t145\t1651\t1549\t1217\t681\t1649\n" +
                    "1723\t1789\t5525\t4890\t3368\t188\t3369\t4842\t3259\t2502\t4825\t163\t146\t2941\t126\t5594\n" +
                    "311\t2420\t185\t211\t2659\t2568\t2461\t231\t2599\t1369\t821\t506\t2227\t180\t220\t1372\n" +
                    "197\t4490\t141\t249\t3615\t3314\t789\t4407\t169\t352\t4383\t5070\t5173\t3115\t132\t3513\n" +
                    "4228\t2875\t3717\t504\t114\t2679\t165\t3568\t3002\t116\t756\t151\t4027\t261\t4813\t2760\n" +
                    "651\t3194\t2975\t2591\t1019\t835\t3007\t248\t3028\t1382\t282\t3242\t296\t270\t3224\t3304\n" +
                    "1858\t1650\t1720\t1848\t95\t313\t500\t1776\t207\t1186\t72\t259\t281\t1620\t79\t77\n" +
                    "3841\t3217\t440\t3481\t3643\t940\t3794\t4536\t1994\t4040\t3527\t202\t193\t1961\t230\t217\n" +
                    "2837\t2747\t2856\t426\t72\t78\t2361\t96\t2784\t2780\t98\t2041\t2444\t1267\t2167\t2480\n" +
                    "411\t178\t4263\t4690\t3653\t162\t3201\t4702\t3129\t2685\t3716\t147\t3790\t4888\t79\t165"

            println(checksum(input))

        }
    }
}