package lodz.jug.kotlin.starter.general

import lodz.jug.kotlin.starter.general.DependenciesDsl.Companion.dependencies

fun main(args: Array<String>) {

    //show import
    dependencies {
        compile("org.apache","commons","5.0")
        compile("com.package","utils","2.1")
        test("org.mockito","core","3.0")
    }

}




class DependenciesDsl{
    data class DependencyMapping(val organization:String, val name:String, val version:String)

    companion object {
        fun dependencies(buildDeps: DependenciesDsl.() -> Unit) {
            val dsl = DependenciesDsl()
            dsl.buildDeps()
//            buildDeps(dsl) //varian 2
            dsl.download()
        }

//        fun dependencies(buildDeps: DependenciesDsl.() -> Unit) =
//                DependenciesDsl().apply(buildDeps).download()
    }


    private var compileDeps = emptyList<DependencyMapping>()
    private var testDeps = emptyList<DependencyMapping>()

    fun compile(organization:String,name:String,version:String ){
        compileDeps += DependencyMapping(organization, name, version)
    }


    fun test(organization:String,name:String,version:String ){
        testDeps += DependencyMapping(organization, name, version)
    }

    private fun download() {
        println("resolving compile dependencies...")
        compileDeps.forEach{println("resolving $it")}

        println("resolving test dependencies...")
        testDeps.forEach{println("resolving $it")}
    }
}