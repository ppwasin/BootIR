package com.boot.gradle.template

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.Assert.*
import org.junit.Test

class TemplateIrPluginIntegrationTest {
    @Test
    fun testDebugLog() {
        debug(name = "Integartion")
    }

    annotation class DebugLog

    @DebugLog
    fun debug(name: String = "World") = "Hello, $name!"
}