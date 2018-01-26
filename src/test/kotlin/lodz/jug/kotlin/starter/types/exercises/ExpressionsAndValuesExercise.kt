package lodz.jug.kotlin.starter.types.exercises

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ExpressionsAndValues : StringSpec() {
    //TRY TO USE NULL UNION OPERATORS LIKE ?. OR ?: AS MUCH AS YOU CAN
    init {
        "Create money from int" {    // EXERCISE1
            Money.of(3).price shouldBe 3
            Money.of(null) shouldBe Money.ZERO
        }.config(enabled = false)

        "Int extension method to money" { // EXERCISE1
            1.toMoney().price shouldBe 1
            10.toMoney().price shouldBe 10
            15.toMoney().price shouldBe 15

            (null as Int?).toMoney() shouldBe Money.ZERO
        }.config(enabled = false)

        "convert nullable string to description" { // EXERCISE2
            "some description".toDescription().text shouldBe "some description"
            (null as String?).toDescription() shouldBe Description.EMPTY
            "".toDescription() shouldBe Description.EMPTY
        }.config(enabled = false)

        "Generate Delivery address in plain text" {  // EXERCISE3
            val fullAddress= Address("Boston","SomeStreet","Massachusetts")
            val notNullAddress= Address("Lodz","Piotrkowska",null)
            val nullAddress:Address?= null

            Shop.generateDeliveryAddress(fullAddress) shouldBe "{city:Boston,street:SomeStreet,state:Massachusetts}"
            Shop.generateDeliveryAddress(notNullAddress) shouldBe "{city:Lodz,street:Piotrkowska,state:EMPTY}"
            Shop.generateDeliveryAddress(nullAddress) shouldBe null
        }.config(enabled = false)

        //sum all prices from products minus discounts (300 -15)+20+50 = 355
        "summary price" { // EXERCISE 4
            // Notice that discount is not percentage but literal value!!
            val p1=Product("TV","great tv",300,15)
            val p2=Product("Book","coaching book",20,null)
            val p3=Product("Keyboard",null,50,null)

            val purchase=Purchase(listOf(p1,p2,p3),null,null)

            Shop.priceSummary(purchase) shouldBe 355
            Shop.priceSummary(null) shouldBe 0
        }.config(enabled = false)

        "send email" {  // EXERCISE5
            val p1=Product("TV","great tv",300,15)

            val purchase=Purchase(listOf(p1),Email("somemail@com"),null)

            Shop.sendEmail(purchase)
            Shop.EmailSender.lastSentWas("somemail@com") shouldBe true

            Shop.sendEmail(null)
            Shop.EmailSender.lastSentWas("admins@shop.com") shouldBe true

            Shop.EmailSender.send(null)
            Shop.EmailSender.lastSentWas("admins@shop.com") shouldBe true
        }.config(enabled = false)
    }
}

class Money private constructor(val price: Int) {
    companion object {
        val ZERO = Money(0)
        fun of(v: Int?):Money = TODO()  //exercise1
    }
}

class Description private constructor(val text: String) {
    companion object {
        val EMPTY = Description("NO DESCRIPTION")
        fun of(text: String): Description = TODO() // EXERCISE2
    }
}

class Product(val name: String, val description: String?, val price: Int, val discount: Int?)
class Email(val address: String)
class Address(val city: String, val street: String, val state: String?)
class Purchase(val products: List<Product>, val email: Email?, val deliverTo: Address?)

object Shop {
    fun generateDeliveryAddress(address: Address?): String? = TODO()


    //sum all prices from products minus discounts
    fun priceSummary(p: Purchase?): Int = TODO() // EXERCISE4

    fun sendEmail(p: Purchase?): Unit = EmailSender.send(TODO()) // EXERCISE5

    object EmailSender {
        private val ADMINS = "admins@shop.com"
        private var sentEmails = emptyList<String>()
        fun send(address: String?): Unit {
          TODO() // EXERCISE5
        }

        fun lastSentWas(address: String): Boolean = sentEmails.first() == address
    }


}

fun Int?.toMoney(): Money = TODO() // EXERCISE1
fun String?.toDescription(): Description = TODO() // EXERCISE2
