package lodz.jug.kotlin.coderetreat.objects

import io.kotlintest.matchers.shouldBe
import io.kotlintest.tables.forAll
import io.kotlintest.tables.table
import io.kotlintest.tables.row
import io.kotlintest.tables.headers
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