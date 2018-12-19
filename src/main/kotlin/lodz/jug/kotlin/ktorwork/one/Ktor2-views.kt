package lodz.jug.kotlin.ktorwork.one

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.html.*
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.*


fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8000, module = Application::simpleHtml).start(wait = true)
}


private fun Application.simpleHtml() {
    routing {
        get("/html1") {
            call.respondHtml(block = Views.html1)
        }

        get("/html2") {
            call.respondHtmlTemplate(Templates.MulticolumnTemplate()) {
                column1 { +"column 1 placeholder" }
                column2 { +"col2 placeholder" }
            }
        }
    }


    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respondHtml {
                body {
                    div { +"NIMA :(" }
                }
            }
        }
    }
}

private object Views {
    val html1: HTML.() -> Unit = {
        head {
            title("First Html")
        }
        body {
            h1(classes = "myClass") {
                +"Title of html1"
            }
        }

    }

}

private object Templates {
    class MainLayout : Template<HTML> {

        val content = Placeholder<HtmlBlockTag>()
//        val menu = TemplatePlaceholder<MenuTemplate>(

        val menu = TemplatePlaceholder<MainMenu>()

        override fun HTML.apply() {
            head {
                title { +"Template" }
            }
            body {
                 div{
                    insert(content)
                }
                insert(MainMenu(), menu)
            }
        }

    }

    class MainMenu : Template<FlowContent> {
        val item = PlaceholderList<UL, FlowContent>()
        override fun FlowContent.apply() {
            if (!item.isEmpty()) displayMenu()(this)
        }

        private fun displayMenu(): FlowContent.() -> Unit = {
            ul {
                each(item) {
                    li {
                        if (it.first) b {
                            insert(it)
                        } else {
                            insert(it)
                        }
                    }
                }
            }
        }
    }

    class MulticolumnTemplate(val main: MainLayout = MainLayout()) : Template<HTML> {
        val column1 = Placeholder<FlowContent>()

        val column2 = Placeholder<FlowContent>()
        override fun HTML.apply() {
            insert(main) {
                menu {
                    item { +"One" }
                    item { +"Two" }
                }
                content {
                    div("column") {
                        insert(column1)
                    }
                    div("column") {
                        insert(column2)
                    }
                }
            }
        }
    }
}
