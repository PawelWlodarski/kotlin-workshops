package lodz.jug.kotlin.reactive.prezka


fun main() {
    Example0.readUserId(){id ->
        Example0.findUser(id){user ->
            val text=Example0.uiRepresentation(user)
            println(text)
        }
    }
}


object Example0{
    fun <A> readUserId(callback:(Int) -> A): Unit {
        Thread.sleep(1000)
        callback(1)
    }

    fun <A> findUser(id: Int,callback:(User) -> A): Unit {
        Thread.sleep(1000)
        callback(User(id, "Roman"))
    }

    fun uiRepresentation(u: User): String = "User[id=${u.id}, name=${u.name}]"


    data class User(val id: Int, val name: String)
}