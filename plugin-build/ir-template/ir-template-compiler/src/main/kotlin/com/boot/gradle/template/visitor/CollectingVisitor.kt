package com.boot.gradle.template.visitor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class CollectingVisitor(
    private val elements: MutableList<IrElement>
) : IrElementVisitor<Unit, Nothing?> {
    override fun visitElement(element: IrElement, data: Nothing?) {
        elements.add(element)
        element.acceptChildren(this, data)
    }
}

@ExperimentalStdlibApi
fun collect(element: IrElement) = buildList {
    element.accept(CollectingVisitor(this), null)
}

@ExperimentalStdlibApi
fun breadthFirstCollect(element: IrElement) = buildList {
    val queue = ArrayDeque<IrElement>()
    val visitor = object : IrElementVisitor<Unit, Nothing?> {
        override fun visitElement(element: IrElement, data: Nothing?) {
            queue.add(element)
        }
    }

    queue.add(element)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        this.add(current) // add element to collection
        current.acceptChildren(visitor, null) // add children to element queue
    }
}