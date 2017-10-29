package lodz.jug.kotlin.starter.oop

import lodz.jug.kotlin.Displayer
import java.util.*

fun main(args: Array<String>) {
    Displayer.header(" KOTLIN STARTER OOP - CLASSES")

    Displayer.section("Declaring java class in kotlin")
    //explain type interference
    //explain syntax format
    //no 'new'
    // no semicolon
    val userJava: UserJava = UserJava("George", 20)
    Displayer.section("userJava.name", userJava.name)


    Displayer.title("secondary constructor")
    val withSecondary = UserTwoConstructors("George")

    Displayer.title("class with init")
    val withInit = UserWithInit("UserWithInit")
    Displayer.section("UserWithInit other property : ${withInit.other}")


    Displayer.title("Encapsulation")
    val account = SocialMediaAccount("George Acc", "", listOf("Steven", "Angela"))
    account.wall = "not encapsulated wall"
    account.addMessage("new message 1")
    account.addMessage("new message 2")
    account.addMessage("new message 3")

    Displayer.section("Stevens account messages", account.getMessages())
}


// explain position of primary constructor
//mvn clean compile with different jvmtargets
class UserKotlin(val name: String, val age: Int) {
    override fun toString() = "KotlineUser($name, $age)" // String interpolation!
}

//class with second constructor
class UserTwoConstructors(val name: String, val city: String) {
    constructor(name: String) : this(name, "LODZ") {
        Displayer.section("Use with two cons created with ($name,Lodz)") //no access to city here
    }
}

class UserWithInit(val name: String, val city: String) {
    val other: String

    constructor(name: String) : this(name, "LODZ") {
        println("in constructor")
    }

    init {
        other = "otherValue"
        println("in init")
    }
}

class SocialMediaAccount(val name: String, var wall: String, friends: List<String>) {
    private var messages: List<String> = java.util.LinkedList()

    //can not use  '=' syntax - not expression
    fun addMessage(m: String) {
        messages += m
    }

    //remove type and show warning
    fun getMessages(): List<String> = Collections.unmodifiableList(messages)
}
