package com.boot.gradle.template.visitor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class StringIndentVisitor : IrElementVisitor<Unit, String> {
    override fun visitElement(element: IrElement, data: String) {
        println("$data..${render(element)} {")
        element.acceptChildren(this, "  $data")
        println("$data}")
    }

    private fun render(element: IrElement) = element.toString()
}