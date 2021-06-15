package com.boot.gradle.template

import com.boot.gradle.template.transformer.DebugLogTransformer
import com.boot.gradle.template.utils.DebugLogUtils
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.interpreter.toIrConst
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.name.FqName

class TemplateIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val string: String,
    private val file: String
) : IrGenerationExtension {
    @ExperimentalStdlibApi
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'string' = $string")
        messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'file' = $file")
//        buildHelloWorld(pluginContext)

//        moduleFragment.accept(RecursiveVisitor(), null)
//        moduleFragment.accept(StringIndentVisitor(), "")
//        collect(moduleFragment)
//        println(moduleFragment.dump())
        val typeAnyNullable = pluginContext.irBuiltIns.anyNType

        val debugLogReference = DebugLogUtils.getDebugLogReference()
        val debugLogAnnotation = pluginContext.referenceClass(FqName(debugLogReference))!!
        val funPrintln = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
            .single {
                val parameters = it.owner.valueParameters
                parameters.size == 1 && parameters[0].type == typeAnyNullable
            }

        moduleFragment.transform(DebugLogTransformer(pluginContext, debugLogAnnotation, funPrintln), null)
    }


}

