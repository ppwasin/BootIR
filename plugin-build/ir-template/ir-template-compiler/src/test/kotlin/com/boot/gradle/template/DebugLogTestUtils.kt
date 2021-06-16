package com.boot.gradle.template

import com.boot.gradle.template.utils.getClassReferenceString
import com.tschuchort.compiletesting.SourceFile

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
}