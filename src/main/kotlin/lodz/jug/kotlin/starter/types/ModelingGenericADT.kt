package lodz.jug.kotlin.starter.types

import lodz.jug.kotlin.Displayer

//typealias!!

fun main(args: Array<String>) {
    Displayer.header("Modeling GENERIC ADT")

    Displayer.title("CONTRAVARIANCE")

    class ContravariantExample<in A>(private val prefix:String){
        fun method(argument:A):Unit = println("$prefix : invariant $argument")
    }

    //change to var for demonstration
    val userVariant:ContravariantExample<User> = ContravariantExample("user")
    val objectVariant:ContravariantExample<Any> = ContravariantExample("object")

    val user:User=User("Stefan9000",30)
    val any=70

    userVariant.method(user)
//    userVariant.method(any)  //ILLEGAL

    objectVariant.method(user)
    objectVariant.method(any)

//    objectVariant=userVariant   //ILLEGAL because you could then pass int to userVariant!

//    userVariant=objectVariant  // LEGAL because you can only pass user to userVariant which is legal for objectVariant


    Displayer.title("CONTRAVARIANCE AND HIGH ORDER FUNCTIONS")
    //definitions under main

    Executor.execute(user,UserConsumer) //legal user in user consumer
//    Executor.execute(any,UserConsumer) //illegal, you can not pass anything to user consumer
    Executor.execute(user,ObjectConsumer) //legal, you can pass user where object is expected
    Executor.execute(any,ObjectConsumer) //legal, you can pass user where object is expected


    Displayer.title("IN,OUT")

    //no SAM conversion
    val userString:MyFunction<User,String> = object : MyFunction<User,String>(){
        override fun invoke(a: User): String = "User(${a.login},${a.age})"
    }

    val anyString:MyFunction<Any,String> = object : MyFunction<Any,String>(){
        override fun invoke(a: Any): String = "Any - $a"
    }

    //notice generics in type and in declaration
    val userObject:MyFunction<User,Any> = object : MyFunction<Any,String>(){
        override fun invoke(a: Any): String = "Any - $a"
    }

    Displayer.title("ALIASES")
    val fa1:FunctionAlias<User,String> = {"User(${it.login},${it.age})"}
    val fa2:FunctionAlias<Any,String> = {"Any($it)"}

    var fa3:FunctionAlias<User,Any> = fa1  //you can easily pass User where Any is expected
//    var fa4:FunctionAlias<Any,String> = fa1  FORBIDDEN because you would be able to pass Int where Use is expected

    val u1:FromUser<String> = {it.login}
    val u2:FromUser<Int> = {it.age}

    Displayer.section("alias1",fa1(user))
    Displayer.section("alias1",fa2(any))
    Displayer.section("short alias1",u1(user))
    Displayer.section("short alias2",u2(user))


    Displayer.title("CONTRA and COVARIANT POSITIONS")
//    class SomeClass<in A,out B>(private val a:A){
//        fun getA():A = a
//        fun printB(a:B) = println(a)
//    }

    //ERROR - in Parameter & out position
    //val e1:SomeClass<Any,Any> = SomeClass<Any,Any>(user)
    //val e1:SomeClass<Int,Any> = e1
    //val i:Int= e1.getA //ILLEGAL !!

    //ERROR - out Parameter & in position
    //val e2:SomeClass<Any,User> = SomeClass<Any,User>(user){
    //  override fun printB(u:User) = println("${u.login}")
    // }
    //val e1:SomeClass<Any,Any> = e2
    //val i:Int= e1.printA(69) //ILLEGAL !!



    Displayer.title("GENERIC BOTTOM SINGLETON")
    //Generic ADT with Bottom variant defined at the bottom of this file


}

//CONTRAVARIANCE AND HIGH ORDER FUNCTIONS
class User(val login:String,val age:Int){
    override fun toString(): String {
        return "User(login='$login', age=$age)"
    }
}


abstract class AbstractConsumer<in A>{
    abstract fun operation(a:A):Unit
}

object UserConsumer: AbstractConsumer<User>(){
    override fun operation(a: User) = println("this is user operation, you can call login,age ${a.login},${a.age}")
}

object ObjectConsumer : AbstractConsumer<Any>(){
    override fun operation(a: Any) = println("this is object operation, not much you can do , $a")
}

object Executor{
    fun <A> execute(a:A, consumer:AbstractConsumer<A>) = consumer.operation(a)
}


//"IN,OUT"
@FunctionalInterface
abstract class MyFunction<in A,out B>{
    abstract operator fun invoke(a:A):B
}

//ALIASES
typealias FunctionAlias<A,B> = (A) -> B
typealias FromUser<A> = (User) -> A

//GENERIC BOTTOM SINGLETON
sealed class SomeAbstractType<out A>
class Variant1<A> :SomeAbstractType<A>()
data class Variant2<A,B>(val something:B) :SomeAbstractType<A>()
object NothingSingletonVariant : SomeAbstractType<Nothing>()
