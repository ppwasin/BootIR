package com.boot.gradle.template.visitor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class RecursiveVisitor : IrElementVisitor<Unit, Nothing?> {
    override fun visitElement(element: IrElement, data: Nothing?) {
        println("$element: $data")
        element.acceptChildren(this, data)
    }
}