package lodz.jug.kotlin.starter.types.answers

import io.kotlintest.specs.StringSpec

class ModelingGenericADTAnswers : StringSpec() {
    init {

    }
}


sealed class ShoppingEvent {
    abstract val id: Int
    abstract val amount: Int
    abstract val products: Sequence<String>
}

typealias ProductName = String
typealias ProductPrice = String

data class StandardShopping(override val id: Int,
                            override val amount: Int,
                            override val products: Sequence<String>) : ShoppingEvent()

data class PromotionalShopping(override val id: Int,
                               override val amount: Int,
                               override val products: Sequence<String>,
                               val discount: Int) : ShoppingEvent()


data class LuxShopping(override val id: Int,
                       override val amount: Int,
                       override val products: Sequence<String>,
                       val luxProducts: Sequence<Pair<ProductName, ProductPrice>>) : ShoppingEvent()




//validation result ADT