package com.boot.gradle.template.transformer

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/*
    fun main() {
      println("Hello, World!")
    }
    * */
fun buildHelloWorld(pluginContext: IrPluginContext) {
    val typeNullableAny = pluginContext.irBuiltIns.anyNType
    val typeUnit = pluginContext.irBuiltIns.unitType

    //Getting the IrFunctionSymbol for the println(message: Any?) function.
    //There are many println() overload, so use `single` filter only one function
    val funPrintln = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].type == typeNullableAny
        }

    val funMain = pluginContext.irFactory.buildFun {
        name = Name.identifier("main")
        visibility = DescriptorVisibilities.PUBLIC // default
        modality = Modality.FINAL // default
        returnType = typeUnit
    }

    funMain.body = DeclarationIrBuilder(pluginContext, funMain.symbol).irBlockBody {
//            val callPrintln = irCall(funPrintln)
//            callPrintln.putValueArgument(0, irString("Hello, World!"))
//            +callPrintln //add to `this` block

        +irCall(funPrintln).also { call ->
            call.putValueArgument(0, irString("Hello, World!"))
        }
    }

    println(funMain.dump())
}