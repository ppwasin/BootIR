package com.boot.gradle.template.visitor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

// Not as efficient as a while loop, but exemplifies how the output type could be used
class RootParentVisitor : IrElementVisitor<IrDeclarationParent?, Nothing?> {
    override fun visitElement(element: IrElement, data: Nothing?): IrDeclarationParent? = null

    override fun visitDeclaration(declaration: IrDeclarationBase, data: Nothing?): IrDeclarationParent {
        val parent = declaration.parent
        return parent.accept(this, null) ?: parent
    }
}