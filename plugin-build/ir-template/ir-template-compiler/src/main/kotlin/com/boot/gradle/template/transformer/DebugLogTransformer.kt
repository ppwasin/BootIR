package com.boot.gradle.template.transformer

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irCatch
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name


class DebugLogTransformer(
    private val pluginContext: IrPluginContext,
    private val annotationClass: IrClassSymbol,
    private val logFunction: IrSimpleFunctionSymbol,
) : IrElementTransformerVoidWithContext() {
    private val typeUnit = pluginContext.irBuiltIns.unitType
    private val typeThrowable = pluginContext.irBuiltIns.throwableType

    private val classMonotonic =
        pluginContext.referenceClass(FqName("kotlin.time.TimeSource.Monotonic"))!!

    private val funMarkNow =
        pluginContext.referenceFunctions(FqName("kotlin.time.TimeSource.markNow"))
            .single()

    private val funElapsedNow =
        pluginContext.referenceFunctions(FqName("kotlin.time.TimeMark.elapsedNow"))
            .single()

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val body = declaration.body
        if (body != null && declaration.hasAnnotation(annotationClass)) {
            declaration.body = irDebug(declaration, body)
        }
        return super.visitFunctionNew(declaration)
    }

    /**
    @DebugLog
    fun greet(greeting: String = "Hello", name: String = "World"): String {
        return "${'$'}greeting, ${'$'}name!"
    }
    >>>
    @DebugLog
    fun greet(greeting: String = "Hello", name: String = "World"): String {
        println("⇢ greet(greeting=$greeting, name=$name)")
        val startTime = TimeSource.Monotonic.markNow()
        try {
            val result = "${'$'}greeting, ${'$'}name!"
            println("⇠ greet [${startTime.elapsedNow()}] = $result")
            return result
        } catch (t: Throwable) {
            println("⇠ greet [${startTime.elapsedNow()}] = $t")
            throw t
        }
    }
     **/
    private fun irDebug(function: IrFunction, body: IrBody): IrBody {
        return DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
            +irDebugEnter(function)

            //startTime = TimeSource.Monotonic.markNow()
            //irTemporary = a temporary local variable
            val startTime = irTemporary(irCall(funMarkNow).also { call ->
                call.dispatchReceiver = irGetObject(classMonotonic)
            })

            //try expression
            val tryBlock = irBlock(resultType = function.returnType) {
                for (statement in body.statements) +statement
                if (function.returnType == typeUnit) +irDebugExit(function, startTime)
            }.transform(DebugLogReturnTransformer(function, startTime), null)

            val throwable = buildVariable(
                scope.getLocalDeclarationParent(),
                startOffset,
                endOffset,
                IrDeclarationOrigin.CATCH_PARAMETER,
                Name.identifier("t"),
                typeThrowable
            )

            +IrTryImpl(startOffset, endOffset, tryBlock.type).also { irTry ->
                irTry.tryResult = tryBlock

                //catch expression
                irTry.catches += irCatch(throwable, irBlock {
                    //to log the exception and then preserve the exception by rethrowing it
                    +irDebugExit(function, startTime, irGet(throwable))
                    +irThrow(irGet(throwable))
                })
            }
        }
    }

    /**
     * println("⇢ greet(greeting=$greeting, name=$name)")
     * **/
    private fun IrBuilderWithScope.irDebugEnter(
        function: IrFunction
    ): IrCall {
        val concat = irConcat()
        concat.addArgument(irString("⇢ ${function.name}("))
        for ((index, valueParameter) in function.valueParameters.withIndex()) {
            if (index > 0) concat.addArgument(irString(", "))
            concat.addArgument(irString("${valueParameter.name}="))
            concat.addArgument(irGet(valueParameter)) //get value of valueParameter
        }
        concat.addArgument(irString(")"))

        return irCall(logFunction).also { call ->
            call.putValueArgument(0, concat)
        }
    }

    /**
     * println("⇠ greet [${startTime.elapsedNow()}] = $result")
     * println("⇠ greet [${startTime.elapsedNow()}] = $t")
     * println("⇠ greet [${startTime.elapsedNow()}]")
     **/
    private fun IrBuilderWithScope.irDebugExit(
        function: IrFunction,
        startTime: IrValueDeclaration,
        result: IrExpression? = null
    ): IrCall {
        val concat = irConcat()
        concat.addArgument(irString("⇠ ${function.name} ["))
        concat.addArgument(irCall(funElapsedNow).also { call ->
            call.dispatchReceiver = irGet(startTime)
        })
        if (result != null) {
            concat.addArgument(irString("] = "))
            concat.addArgument(result)
        } else {
            concat.addArgument(irString("]"))
        }

        return irCall(logFunction).also { call ->
            call.putValueArgument(0, concat)
        }
    }

    /**
     * convert return statements so the result can be logged before exiting the function.
     * val result = "${'$'}greeting, ${'$'}name!"
     * println("⇠ greet [${startTime.elapsedNow()}] = $result")
     * return result
     * */
    inner class DebugLogReturnTransformer(
        private val function: IrFunction,
        private val startTime: IrVariable
    ) : IrElementTransformerVoidWithContext() {
        override fun visitReturn(expression: IrReturn): IrExpression {
            if (expression.returnTargetSymbol != function.symbol) return super.visitReturn(
                expression
            )

            return DeclarationIrBuilder(pluginContext, function.symbol).irBlock {
                val result = irTemporary(expression.value)
                +irDebugExit(function, startTime, irGet(result))
                +expression.apply {
                    value = irGet(result)
                }
            }
        }
    }

}


