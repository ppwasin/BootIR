package com.boot.gradle.template

import com.boot.gradle.template.DebugLogTestUtils.getDebugLogReference
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/*
* TODO: Add actual test
* */
class IrPluginTest {

    @Test
    fun `IR plugin success`() {
        val result =
            compile(
                sourceFile =
                SourceFile.kotlin(
                    name = "main.kt",
                    contents =
                    """
        import ${DebugLogTestUtils.getDebugLogReference()}
        
        fun main() {
            println(debug())
        }
        @DebugLog
        fun debug(name: String = "World") = 
            "Hello, ${'$'}name!"
    """.trimIndent()
                ),
                TemplateComponentRegistrar(true)
            )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `Test DebugLog`() {
        val result =
            compile(
                sourceFile =
                SourceFile.kotlin(
                    "main.kt",
                    """
      import ${DebugLogTestUtils.getDebugLogReference()}
      
      fun main() {
        println(greet())
        println(greet(name = "Kotlin IR"))
      }
      
      @DebugLog
      fun greet(greeting: String = "Hello", name: String = "World"): String { 
        Thread.sleep(15) // simulate work
        return "${'$'}greeting, ${'$'}name!"
      }
    """.trimIndent()
                ),
                TemplateComponentRegistrar(true)
            )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val kClazz = result.classLoader.loadClass("MainKt")
        val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
        main.invoke(null)
    }

    //Follow: https://github.com/bnorm/kotlin-power-assert/blob/master/kotlin-power-assert-plugin/src/test/kotlin/com/bnorm/power/AssertLibraryTest.kt
    @Test
    fun runEnable() {
//        executeAssertion(source = DebugLogTestUtils.main, TemplateComponentRegistrar(true))
//        assertMessage(
//            source =
//            """
//            import ${getDebugLogReference()}
//
//            fun main() {
//                println(greet(name = "Kotlin IR"))
//            }
//
//            @DebugLog
//            fun greet(greeting: String = "Hello", name: String = "World"): String {
//                Thread.sleep(15) // simulate work
//                return "            ${'$'}            greeting,             ${'$'}            name!"
//            }
//            """,
//            """
//                ⇢ greet(greeting=Hello, name=Kotlin IR)
//                ⇠ greet [19.8ms] = Hello, Kotlin IR!
//                Hello, Kotlin IR!
//            """.trimIndent()
//        )
    }
}

//private fun compile(
//    sourceFiles: List<SourceFile>,
//    plugin: ComponentRegistrar = TemplateComponentRegistrar(),
//): KotlinCompilation.Result {
//    return KotlinCompilation()
//        .apply {
//            sources = sourceFiles
//            useIR = true
//            compilerPlugins = listOf(plugin)
//            inheritClassPath = true
//        }
//        .compile()
//}
//
//private fun compile(
//    sourceFile: SourceFile,
//    plugin: ComponentRegistrar = TemplateComponentRegistrar(),
//): KotlinCompilation.Result {
//    return compile(listOf(sourceFile), plugin)
//}
