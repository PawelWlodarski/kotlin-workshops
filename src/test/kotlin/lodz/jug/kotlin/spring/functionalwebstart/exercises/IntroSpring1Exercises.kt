package lodz.jug.kotlin.spring.functionalwebstart.exercises

import io.kotlintest.shouldBe
import lodz.jug.kotlin.spring.functionalwebstart.exercises.ArticleDsl.Companion.GENERIC_CONTENT
import lodz.jug.kotlin.spring.functionalwebstart.exercises.ArticleDsl.Companion.GENERIC_TITLE
import org.junit.jupiter.api.Test

class IntroSpring1Exercises{

    @Test
    fun buildNews() {

        val news=news {
            article {
                titled("My first article")
                withContent("First article content")
            }

            article {
                titled("Learning Kotlin DSL")
                withContent("To create Dsl...")
            }
        }


        news.content shouldBe  listOf(
                Article(title = "My first article" , content = "First article content"),
                Article(title = "Learning Kotlin DSL" , content = "To create Dsl...")
        )
    }


    @Test
    fun buildNewsWithDefaultValues() {

        val news=news {
            article {
                withContent("First article content")
            }

            article {}
        }


        news.content shouldBe  listOf(
                Article(title = GENERIC_TITLE , content = "First article content"),
                Article(title = GENERIC_TITLE , content = GENERIC_CONTENT)
        )
    }

}


fun news(builder : NewsBuildingDsl.() -> Unit):News = NewsBuildingDsl().apply(builder).news

class NewsBuildingDsl {

    private var articles = emptyList<Article>()

    val news:News
        get() = News(articles)


    fun article(builder: ArticleDsl.() -> Unit){
          TODO("provide your DSL here")
    }
}

class ArticleDsl{


    private var content:String? = null
    private var title:String? = null

    val article:Article
        get() = TODO("return article in proper form here")

    fun titled(t:String){TODO("set title here")}
    fun withContent(c:String){TODO("set content here")}


    companion object {
        const val GENERIC_TITLE = "GENERIC TITLE"
        const val GENERIC_CONTENT = "GENERIC CONTENT"
    }
}

data class News(val content:Iterable<Article>)
data class Article(val title:String, val content:String)
