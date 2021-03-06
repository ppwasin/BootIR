package com.boot.gradle.template

import com.boot.gradle.template.transformer.DebugLogTransformer
import com.boot.gradle.template.utils.getClassReference
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.name.FqName

class TemplateIrGenerationExtension(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    @ExperimentalStdlibApi
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        messageCollector.report(CompilerMessageSeverity.INFO, "isEnable")
//        buildHelloWorld(pluginContext)

//        moduleFragment.accept(RecursiveVisitor(), null)
//        moduleFragment.accept(StringIndentVisitor(), "")
//        collect(moduleFragment)
//        println(moduleFragment.dump())
        val typeAnyNullable = pluginContext.irBuiltIns.anyNType

        val debugLogAnnotation = pluginContext.getClassReference<DebugLog>()
        val funPrintln = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
            .single {
                val parameters = it.owner.valueParameters
                parameters.size == 1 && parameters[0].type == typeAnyNullable
            }

        moduleFragment.transform(DebugLogTransformer(pluginContext, debugLogAnnotation, funPrintln), null)
    }


}

