package com.boot.gradle.template

import com.strobel.assembler.InputTypeLoader
import com.strobel.assembler.metadata.ArrayTypeLoader
import com.strobel.assembler.metadata.CompositeTypeLoader
import com.strobel.assembler.metadata.ITypeLoader
import com.strobel.decompiler.Decompiler
import com.strobel.decompiler.DecompilerSettings
import com.strobel.decompiler.PlainTextOutput
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.StringWriter
import java.lang.reflect.InvocationTargetException
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.utils.indexOfFirst
import org.junit.jupiter.api.Assertions.assertEquals

fun assertFunction(javaCode: String, functionStatement: String, expectedFunction: String) {
    assertEquals(expectedFunction, fetchMethodByPrefix(javaCode, functionStatement))
}

fun fetchMethodByPrefix(classText: String, methodSignaturePrefix: String): String {
    val classLines = classText.split("\n")
    val methodSignaturePredicate: (String) -> Boolean = { line -> line.contains(methodSignaturePrefix) }
    val methodFirstLineIndex = classLines.indexOfFirst(methodSignaturePredicate)

    check(methodFirstLineIndex != -1) {
        "Method with prefix '$methodSignaturePrefix' not found within class:\n$classText"
    }

    val multiplePrefixMatches = classLines
        .indexOfFirst(methodFirstLineIndex + 1, methodSignaturePredicate)
        .let { index -> index != -1 }

    check(!multiplePrefixMatches) {
        "Multiple methods with prefix '$methodSignaturePrefix' found within class:\n$classText"
    }

    val indentationSize = classLines[methodFirstLineIndex].takeWhile { it == ' ' }.length

    var curleyBraceCount = 1
    var currentLineIndex: Int = methodFirstLineIndex + 1

    while (curleyBraceCount != 0 && currentLineIndex < classLines.lastIndex) {
        if (classLines[currentLineIndex].contains("{")) {
            curleyBraceCount++
        }
        if (classLines[currentLineIndex].contains("}")) {
            curleyBraceCount--
        }
        currentLineIndex++
    }

    return classLines
        .subList(methodFirstLineIndex, currentLineIndex)
        .joinToString("\n") { it.substring(indentationSize) }
}

fun compile(
    sourceFiles: List<SourceFile>,
    plugin: ComponentRegistrar,
    processorBuilder: List<CommandLineProcessorBuilder> = emptyList()
): KotlinCompilation.Result {
    return KotlinCompilation().apply {
        sources = sourceFiles
        useIR = true
        compilerPlugins = listOf(plugin)
        inheritClassPath = true
        verbose = false

        val processors = processorBuilder.map { it.processor }
        val options = processorBuilder.flatMap { it.options }
        commandLineProcessors = processors
        pluginOptions = options
    }.compile()
}

fun invokeMain(result: KotlinCompilation.Result, className: String): String {
    val oldOut = System.out
    try {
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer, false, "UTF-8"))

        try {
            val kClazz = result.classLoader.loadClass(className)
            val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
            main.invoke(null)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }

        return buffer.toString("UTF-8")
    } finally {
        System.setOut(oldOut)
    }
}

fun KotlinCompilation.Result.javaCode(className: String): String {
    val decompilerSettings = DecompilerSettings.javaDefaults().apply {
        typeLoader = CompositeTypeLoader(*(mutableListOf<ITypeLoader>()
            .apply {
                // Ensure every class is available.
                generatedFiles.forEach {
                    add(ArrayTypeLoader(it.readBytes()))
                }

                // Loads any standard classes already on the classpath.
                add(InputTypeLoader())
            }
            .toTypedArray()))

        isUnicodeOutputEnabled = true
    }

    return StringWriter().let { writer ->
        Decompiler.decompile(
            className,
            PlainTextOutput(writer).apply { isUnicodeOutputEnabled = true },
            decompilerSettings
        )
        writer.toString().trimEnd().trimIndent()
    }
}