package lodz.jug.kotlin.reactive.spring.reactor.lab.multiplesources

import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*


typealias Profile = String
typealias Salary = Int

fun main(args: Array<String>) {
    val dao = BlockingDao()
    val repo = AsyncRepo(dao)


    //zip with explicit empty domain types
    val p1 = repo.getProfile(1)
    val p2 = repo.getProfile(2)
    val p3: Mono<Optional<Profile>> = Mono.empty()
    val s = repo.getSalaries()

    Mono.zip(p1, p2, s)
            .doOnSuccess { println("handle null here  : $it") }
            .subscribe { zipped -> Domain.domainLogicWithOptionals(zipped.t1, zipped.t2, zipped.t3) }

    //optional blocking result
    val r=Mono.zip(p1, p3, s)
            .blockOptional()

    println("blocked optional $r")

    //Empty example
    Mono.zip(p1, p3, s)
            .switchIfEmpty(Mono.error(IllegalArgumentException("not working")))
            .subscribe(
            {zipped -> Domain.domainLogicWithOptionals(zipped.t1, zipped.t2, zipped.t3)},
            {error -> println("error occured $error")},
            {println("Empty example just Completed")}
    )

    //merge multiple values
    //val r: Flux<Optional<Profile>> = repo.getProfile(1).mergeWith(repo.getProfile(2))


}

object Domain {
    fun domainLogicWithOptionals(p1: Optional<Profile>, p2: Optional<Profile>, s: List<Salary>) {
        println("domain method called with : $p1,$p2,$s")
    }
}

class AsyncRepo(private val dao: BlockingDao) {

    //is this a good idea?
    private val elasticBlockingScheduler = Schedulers.newElastic("blocking")

//    private val fixedBlockingScheduler = Schedulers.newParallel("blocking",100)

    //approach two
    private val NULL_PROFILE = ""

    //approach one - the same as with empty list: option - zero-one element collection
    fun getProfile(id: Int): Mono<Optional<Profile>> =
            Mono.fromSupplier { Optional.ofNullable(dao.getProfile(id)) }
                    .subscribeOn(elasticBlockingScheduler)

    fun getProfileNULLProfile(id: Int): Mono<Profile> =
            Mono.fromSupplier { dao.getProfile(id) }
                    .map { it ?: NULL_PROFILE }
                    .subscribeOn(elasticBlockingScheduler)

    fun getProfileEmpty(id: Int): Mono<Profile> =
            Mono.fromSupplier { dao.getProfile(id) }.flatMap {
                if (it == null) Mono.empty() else Mono.just(it)
            }
                    .subscribeOn(elasticBlockingScheduler)

    fun getSalaries(): Mono<List<Salary>> =
            Mono.create {sink ->
                val salaries=dao.getSalaries() ?: emptyList()
                sink.success(salaries)
            }
}

class BlockingDao {
    fun getProfile(id: Int): Profile? {
        Thread.sleep(500)
        println("returning profile for id=$id")
        return if (id % 2 == 0) "PROFILE" else null
    }

    fun getSalaries(): List<Salary>? {
        Thread.sleep(700)
        println("returning salaries")
        return listOf(1, 2, 3)
    }
}