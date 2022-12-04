package ru.brightos.oop8.utils.tree

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.view.SelectableView

interface Observable<E> {
    fun notifyObservers()
    fun registerObserver(observer: (E) -> Unit)
    fun deleteAllObservers()
}