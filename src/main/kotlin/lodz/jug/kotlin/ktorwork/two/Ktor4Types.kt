package lodz.jug.kotlin.ktorwork.two

import arrow.core.Failure
import arrow.core.Try
import arrow.core.getOrElse
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.gson.gson
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.DataConversionException

fun main(args: Array<String>) {
    val server=embeddedServer(Netty, 8080,
            module = Application::module4,
            watchPaths = listOf("classes")
    ).start(true)


    println("CLASS LOADER : " + server::class.java.classLoader.getResource("."))
}


//"io.ktor:ktor-locations:$ktor_version"
@KtorExperimentalLocationsAPI
fun Application.module4(){
    install(CallLogging){
        level = org.slf4j.event.Level.DEBUG
    }
    install(Locations)
    install(ContentNegotiation){
        gson{
            setPrettyPrinting()
        }
    }
    install(DataConversion){
        convert<Module4Protocol.Price>{
            decode{ values: List<String>, _ ->
                Module4Protocol.Price(values.singleOrNull()).getOrElse { throw it }.also {// 'also' native function
                    log.debug("price converted to $it")
                }

            }
            encode {
                when(it){
                    null -> listOf()
                    is Module4Protocol.Price -> listOf(it.money.toString())
                    else -> throw DataConversionException("expected instance of Price but was ${it.javaClass}")
                }
            }
        }


    }

    //important : this is not routing.get but locations.get : import io.ktor.locations.get
    // also : https://github.com/ktorio/ktor/issues/368
    routing {
//        trace { application.log.debug(it.buildText()) }

        get<Module4Protocol.GetInfo>{getInfo ->
            call.respondText("info for id : ${getInfo.id} with order ${getInfo.order}" )
        }

        //http://localhost:8080/search/p1/all
        get<Module4Protocol.ProductName.All>{nameCriteria ->
            call.respond(ProductsDb.find(nameCriteria.name.nameLiteral))
        }

        get<Module4Protocol.ProductName.Advanced>{advancedCriteria ->
            call.respond(ProductsDb.find(advancedCriteria.name.nameLiteral,advancedCriteria.maxPrice))
        }
    }
}

@KtorExperimentalLocationsAPI
object ProductsDb{
    data class Product(val name:String,val color:String,val price:Int)
    private val products= listOf(
            Product("p1","red",10),
            Product("p1","green",15),
            Product("p1","blue",20),
            Product("p2","green",50),
            Product("p2","blue",60),
            Product("p3","blue",100)
    ).groupBy { it.name }

    fun find(name:String): List<Product> = products[name] ?: emptyList()
    fun find(name:String, maxPrice: Module4Protocol.Price) = find(name).filter { it.price <= maxPrice.money }


}

@KtorExperimentalLocationsAPI  // <- locations are experimental
object Module4Protocol{
    @Location("/info/{id}")
    data class GetInfo(val id:Int, val order:String) //NAMES ARE IMPORTANT!!!

    @Location("/search/{nameLiteral}") data class ProductName(val nameLiteral: String) { //param names matters
        @Location("/all") data class All(val name: ProductName)
        @Location("/advanced/{maxPrice}") data class Advanced(val name: ProductName, val maxPrice: Price)
    }


    class Price private constructor(val money:Int){
        companion object {
            operator fun invoke(s:String?): Try<Price> =
                    if(s == null) Failure(DataConversionException("input for price was null"))
                    else Try{ Price(s.toInt())}
        }

        override fun toString(): String {
            return "Price(money=$money)"
        }


    }
}