package com.boot.gradle.template

import com.boot.gradle.template.utils.getClassReferenceString
import com.tschuchort.compiletesting.PluginOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

object DebugLogTestUtils {

    fun getDebugLogReference() = getClassReferenceString<DebugLog>()

    val main = """
import ${getDebugLogReference()}
fun main() {
    println(greet(name = "Kotlin IR"))
}

@DebugLog
fun greet(greeting: String = "Hello", name: String = "World"): String { 
    Thread.sleep(15) // simulate work
    return "${'$'}greeting, ${'$'}name!"
}
"""

    fun createEnableOptions(isEnable: Boolean): CommandLineProcessorBuilder {
        val processor = TemplateCommandLineProcessor()
        val key = TemplateCommandLineProcessor.ARG_ENABLE
        val option = PluginOption(processor.pluginId, key.toString(), isEnable.toString())

        return object : CommandLineProcessorBuilder {
            override val processor: CommandLineProcessor = processor
            override val options: List<PluginOption> = listOf(option)
        }
    }
}