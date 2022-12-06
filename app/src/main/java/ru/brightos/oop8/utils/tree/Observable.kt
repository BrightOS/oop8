package ru.brightos.oop8.utils.tree

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.view.SelectableView

interface Observable<E> {
    fun notifyObservers()
    fun registerObserver(id: Long = 0L, observer: (E) -> Unit)
    fun deleteAllObservers()
}