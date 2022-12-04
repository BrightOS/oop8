package ru.brightos.oop8.data

import ru.brightos.oop8.model.NodeModel
import java.lang.IndexOutOfBoundsException
import kotlin.math.max
import kotlin.math.min

class ExtendedList<E> {
    constructor()

    constructor(elements: List<E>) {
        elements.forEach {
            add(it)
        }
    }

    constructor(other: ExtendedList<E>) {
        other.forEach {
            add(it)
        }
    }

    private var firstNode: NodeModel<E>? = null
    private var lastNode: NodeModel<E>? = null

    val first: E?
        get() = firstNode?.value

    val last: E?
        get() = lastNode?.value

    val isEmpty: Boolean
        get() = size < 1

    var size: Int = 0

    fun minOf(selector: (E) -> Float): Float {
        var min = Float.MAX_VALUE
        forEach {
            min = min(selector(it), min)
        }
        return min
    }

    fun maxOf(selector: (E) -> Float): Float {
        var max = Float.MIN_VALUE
        forEach {
            max = max(selector(it), max)
        }
        return max
    }

    fun add(element: E) {
        val newNodeModel = NodeModel(element)

        if (lastNode != null) {
            newNodeModel.previous = lastNode
            lastNode?.next = newNodeModel
        } else
            firstNode = newNodeModel

        lastNode = newNodeModel
        size++
    }

    fun add(index: Int, element: E) {
        if (index >= size)
            return add(element)

        if (index < 0)
            throw IndexOutOfBoundsException()

        val previous = if (index > 0) getNodeModel(index - 1) else null
        val next = if (previous != null) previous.next else firstNode
        if (index == 0)
            firstNode = NodeModel(element).apply {
                this.next = next
            }
        val newNodeModel = NodeModel(element).apply {
            this.previous = previous
            this.next = next
        }
        size++
        previous?.next = newNodeModel
        next?.previous = newNodeModel
    }

    private fun getNodeModel(index: Int): NodeModel<E>? {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException()

        var node: NodeModel<E>?

        if (index < size / 2) {
            node = firstNode
            var _index = 0
            while (_index++ != index) {
                node = node?.next
            }
        } else {
            node = lastNode
            var _index = size - 1
            while (_index-- != index) {
                node = node?.previous
            }
        }

        return node
    }

    operator fun get(index: Int): E = getNodeModel(index)!!.value

    fun indexOfFirst(predicate: (E) -> Boolean): Int {
        var node = firstNode
        var index = 0

        while (node?.value?.let { predicate(it) } == false && index < size) {
            node = node.next
            index++
        }

        return if (index == size) -1 else index
    }

    fun countIf(predicate: (E) -> Boolean): Int {
        var result = 0
        forEach {
            if (predicate(it))
                result++
        }
        return result
    }

    fun clear() {
        firstNode = null
        lastNode = null
        size = 0
    }

    fun removeAt(index: Int) {
        if (index < 0 || index >= size)
            throw IndexOutOfBoundsException()

        if (index == 0)
            return removeFirst()

        if (index == size - 1)
            return removeLast()

        var node: NodeModel<E>?

        if (index < size / 2) {
            node = firstNode
            var _index = 0
            while (_index++ != index) {
                node = node?.next
            }
        } else {
            node = lastNode
            var _index = size - 1
            while (_index-- != index) {
                node = node?.previous
            }
        }

//        println("${node?.previous?.next?.value} ${node?.next?.value} ${node?.next?.previous?.value} ${node?.previous?.value}")
        node?.previous?.next = node?.next
        if (node?.previous?.next == null)
            firstNode = node?.next
        node?.next?.previous = node?.previous
//        println("${node?.previous?.next?.value} ${node?.next?.value} ${node?.next?.previous?.value} ${node?.previous?.value}")
        size--
    }

    fun removeFirst() {
        firstNode = firstNode?.next.apply {
            this?.previous = null
        }
        size--
        if (size == 0)
            lastNode = null
    }

    fun removeLast() {
        lastNode = lastNode?.previous.apply {
            this?.next = null
        }
        size--
        if (size == 0)
            firstNode = null
    }

    fun popFirst(): E? {
        val returnValue = first
        removeFirst()
        return returnValue
    }

    fun popLast(): E? {
        val returnValue = last
        removeLast()
        return returnValue
    }

    fun remove(element: E) {
        var node = firstNode

        if (node?.value == element) {
            firstNode = node?.next
            size--
            return
        }

        while (node?.next != element && node?.next != null)
            node = node.next

        if (node?.next != null) {
            node.next = node.next?.next
            size--
        }
    }

    fun forEach(action: (element: E) -> Unit) {
        repeat(size) {
            action(get(it))
        }
    }

    fun forEachIndexed(action: (index: Int, element: E) -> Unit) {
        if (size > 0)
            repeat(size) {
                action(it, get(it))
            }
    }

    override fun toString(): String {
        var result = "["
        forEach {
            result += "$it, "
        }
        if (result.length > 1)
            result = result.dropLast(2)
        result += "]"
        return result
    }
}