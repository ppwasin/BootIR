package com.boot.gradle.template

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DebugLogIRTest {

    private val main = SourceFile.kotlin(
        "main.kt", """
import ${DebugLogTestUtils.getDebugLogReference()}
fun main() {
    println(greet())
    println(greet(name = "Kotlin IR"))
    doSomething()
}
@DebugLog
fun greet(greeting: String = "Hello", name: String = "World"): String {
    Thread.sleep(15)
    return "${'$'}greeting, ${'$'}name!"
}
@DebugLog
fun doSomething() {
    Thread.sleep(15)
}
"""
    )

    @Test
    fun `IR plugin enabled`() {
        val result = compile(sourceFile = main, TemplateComponentRegistrar(true))
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val javaCode = result.javaCode("MainKt")
        println(javaCode)

        assertFunction(
            javaCode, "public static final String greet",
            """
          public static final String greet(@NotNull final String greeting, @NotNull final String name) {
              Intrinsics.checkNotNullParameter(greeting, "greeting");
              Intrinsics.checkNotNullParameter(name, "name");
              System.out.println((Object)("⇢ greet(greeting=" + greeting + ", name=" + name + ')'));
              final TimeMark markNow = TimeSource.Monotonic.INSTANCE.markNow();
              try {
                  Thread.sleep(15L);
                  final String string = greeting + ", " + name + '!';
                  System.out.println((Object)("⇠ greet [" + (Object)Duration.toString-impl(markNow.elapsedNow-UwyO8pc()) + "] = " + string));
                  return string;
              }
              catch (Throwable t) {
                  System.out.println((Object)("⇠ greet [" + (Object)Duration.toString-impl(markNow.elapsedNow-UwyO8pc()) + "] = " + t));
                  throw t;
              }
          }
          """.trimIndent()
        )

        assertFunction(
            javaCode, "public static final void doSomething",
            """
          public static final void doSomething() {
              System.out.println((Object)"⇢ doSomething()");
              final TimeMark markNow = TimeSource.Monotonic.INSTANCE.markNow();
              try {
                  Thread.sleep(15L);
                  System.out.println((Object)("⇠ doSomething [" + (Object)Duration.toString-impl(markNow.elapsedNow-UwyO8pc()) + ']'));
              }
              catch (Throwable t) {
                  System.out.println((Object)("⇠ doSomething [" + (Object)Duration.toString-impl(markNow.elapsedNow-UwyO8pc()) + "] = " + t));
                  throw t;
              }
          }
          """.trimIndent()
        )

        val out = invokeMain(result, "MainKt").trim().split("""\r?\n+""".toRegex())
        out.forEachIndexed { index, s ->
            println("[$index] $s")
        }
        assert(out.size == 8)
        assert(out[0] == "⇢ greet(greeting=Hello, name=World)")
        assert(out[1].matches("⇠ greet \\[\\d+(\\.\\d+)?ms] = Hello, World!".toRegex()))
        assert(out[2] == "Hello, World!")
        assert(out[3] == "⇢ greet(greeting=Hello, name=Kotlin IR)")
        assert(out[4].matches("⇠ greet \\[\\d+(\\.\\d+)?ms] = Hello, Kotlin IR!".toRegex()))
        assert(out[5] == "Hello, Kotlin IR!")
        assert(out[6] == "⇢ doSomething()")
        assert(out[7].matches("⇠ doSomething \\[\\d+(\\.\\d+)?ms]".toRegex()))
    }

    @Test
    fun `IR plugin disabled`() {
        val result = compile(sourceFile = main, TemplateComponentRegistrar(false))
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

        val javaCode = result.javaCode("MainKt")

        assertFunction(javaCode, "public static final String greet",
            """
      public static final String greet(@NotNull final String greeting, @NotNull final String name) {
          Intrinsics.checkNotNullParameter(greeting, "greeting");
          Intrinsics.checkNotNullParameter(name, "name");
          Thread.sleep(15L);
          return greeting + ", " + name + '!';
      }
      """.trimIndent())

        assertFunction(javaCode, "public static final void doSomething",
            """
      public static final void doSomething() {
          Thread.sleep(15L);
      }
      """.trimIndent())

        val out = invokeMain(result, "MainKt").trim().split("""\r?\n+""".toRegex())
        assertTrue(out.size == 2)
        assertTrue(out[0] == "Hello, World!")
        assertTrue(out[1] == "Hello, Kotlin IR!")
    }
}
