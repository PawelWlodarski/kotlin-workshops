package lodz.jug.kotlin.coderetreat.objects

abstract class Cell{
    abstract fun evolve(ns:Int):Cell
}

object LiveCell : Cell(){
    private val evolution= mapOf(2 to LiveCell,3 to LiveCell)

    override fun evolve(ns:Int): Cell = evolution.getOrDefault(ns,DeadCell)
}

object DeadCell : Cell(){
    override fun evolve(ns: Int): Cell {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}