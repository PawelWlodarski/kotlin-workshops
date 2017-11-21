package lodz.jug.kotlin.coderetreat.objects

import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.StringSpec

class  CellTest : StringSpec(){
    init {
        "live cell with 2 or 3 neighbours should live in next round" {
            val ns = table(
                    headers("number of neighbours"),
                    row(2),
                    row(3)
            )

            forAll(ns){neighbours : Int ->
                LiveCell.evolve(neighbours) shouldBe LiveCell
            }
        }
    }
}