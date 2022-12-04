package ru.brightos.oop8.utils.tree

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.view.SelectableView

interface Observable {
    fun notifyObservers()
    fun registerObserver(observer: (ExtendedList<SelectableView>) -> Unit)
    fun deleteAllObservers()
}