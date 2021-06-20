package com.boot.gradle.template

import com.tschuchort.compiletesting.PluginOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

interface CommandLineProcessorBuilder {
    val processor: CommandLineProcessor
    val options: List<PluginOption>
}