package com.boot.gradle.template.utils

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.FqName

inline fun <reified T> IrPluginContext.getClassReference(): IrClassSymbol {
    val classPackage = getClassReferenceString<T>()
    return referenceClass(FqName(classPackage))!!
}

//com.boot.gradle.template.DebugLog
inline fun <reified T> getClassReferenceString(): String {
    return T::class.toString().split(" ")[1]
}