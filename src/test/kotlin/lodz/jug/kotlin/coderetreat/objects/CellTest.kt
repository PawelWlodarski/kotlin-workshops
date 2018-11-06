package lodz.jug.kotlin.coderetreat.objects

import io.kotlintest.shouldBe
import io.kotlintest.tables.forAll
import io.kotlintest.tables.table
import io.kotlintest.tables.row
import io.kotlintest.tables.headers
import io.kotlintest.specs.StringSpec

class  CellTest : StringSpec(){
    init {
        "live cell with 2 or 3 neighbours should live in next round" {
            val ns = table(
                    headers("number of neighbours","expected cell state"),
                    row(1 , DeadCell),
                    row(2 , LiveCell),
                    row(3, LiveCell),
                    row(4, DeadCell)
            )

            forAll(ns){neighbours : Int, expectedState:Cell ->
                LiveCell.evolve(neighbours) shouldBe expectedState
            }
        }


        "dead cell with 3 neighbours is alive" {
            val ns = table(
                    headers("number of neighbours","expected cell state"),
                    row(1 , DeadCell),
                    row(2 , DeadCell),
                    row(3, LiveCell),
                    row(4, DeadCell)
            )

            forAll(ns){neighbours : Int, expectedState:Cell ->
                DeadCell.evolve(neighbours) shouldBe expectedState
            }
        }

    }
}