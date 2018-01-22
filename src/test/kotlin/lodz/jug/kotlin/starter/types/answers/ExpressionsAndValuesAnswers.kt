package lodz.jug.kotlin.starter.types.answers

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ExpressionsAndValues : StringSpec() {
    //TRY TO USE NULL UNION OPERATORS LIKE ?. OR ?: AS MUCH AS YOU CAN
    init {
        "Create money from int" {
            Money.of(3).price shouldBe 3
            Money.of(null) shouldBe Money.ZERO
        }

        "Int extension method to money" {
            1.toMoney().price shouldBe 1
            10.toMoney().price shouldBe 10
            15.toMoney().price shouldBe 15

            (null as Int?).toMoney() shouldBe Money.ZERO
        }

        "convert nullable string to description" {
            "some description".toDescription().text shouldBe "some description"
            (null as String?).toDescription() shouldBe Description.EMPTY
        }

        "Generate Delivery address in plain text" {
            val fullAddress= Address("Boston","SomeStreet","Massachusetts")
            val notNullAddress= Address("Lodz","Piotrkowska",null)
            val nullAddress:Address?= null

            Shop.generateDeliveryAddress(fullAddress) shouldBe "{city:Boston,street:SomeStreet,state:Massachusetts}"
            Shop.generateDeliveryAddress(notNullAddress) shouldBe "{city:Lodz,street:Piotrkowska,state:EMPTY}"
            Shop.generateDeliveryAddress(nullAddress) shouldBe null
        }

        //sum all prices from products minus discounts (300 -15)+20+50 = 355
        "summary price" {

            val p1=Product("TV","great tv",300,15)
            val p2=Product("Book","coaching book",20,null)
            val p3=Product("Keyboard",null,50,null)

            val purchase=Purchase(listOf(p1,p2,p3),null,null)

            Shop.priceSummary(purchase) shouldBe 355
            Shop.priceSummary(null) shouldBe 0
        }

        "send email" {
            val p1=Product("TV","great tv",300,15)

            val purchase=Purchase(listOf(p1),Email("somemail@com"),null)

            Shop.sendEmail(purchase)
            Shop.EmailSender.lastSentWas("somemail@com") shouldBe true

            Shop.sendEmail(null)
            Shop.EmailSender.lastSentWas("admins@shop.com") shouldBe true

            Shop.EmailSender.send(null)
            Shop.EmailSender.lastSentWas("admins@shop.com") shouldBe true
        }
    }
}

class Money private constructor(val price: Int) {
    companion object {
        val ZERO = Money(0)
        fun of(v: Int?) = if (v == null || v == 0) ZERO else Money(v)  //exercise
    }
}

class Description private constructor(val text: String) {
    companion object {
        val EMPTY = Description("NO DESCRIPTION")
        fun of(text: String): Description = if (text.isBlank()) EMPTY else Description(text)
    }
}

class Product(val name: String, val description: String?, val price: Int, val discount: Int?)
class Email(val address: String)
class Address(val city: String, val street: String, val state: String?)
class Purchase(val products: List<Product>, val email: Email?, val deliverTo: Address?)

object Shop {
    fun generateDeliveryAddress(address: Address?): String? =
            if (address != null) {
                val city = address.city
                val street = address.street
                val state = address.state ?: "EMPTY"

                "{city:$city,street:$street,state:$state}"
            } else null


    //sum all prices from products minus discounts
    fun priceSummary(p: Purchase?): Int = p?.products?.map { it.price - (it.discount ?: 0) }?.reduce(Integer::sum) ?: 0

    fun sendEmail(p: Purchase?): Unit = EmailSender.send(p?.email?.address)

    object EmailSender {
        private val ADMINS = "admins@shop.com"
        private var sentEmails = emptyList<String>()
        fun send(address: String?): Unit {
            val sentTo = address ?: ADMINS
            sentEmails = listOf(sentTo) + sentEmails
        }

        fun lastSentWas(address: String): Boolean = sentEmails.first() == address
    }


}

fun Int?.toMoney(): Money = Money.of(this)
fun String?.toDescription(): Description = if (this == null) Description.of("") else Description.of(this)
