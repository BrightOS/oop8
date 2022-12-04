package ru.brightos.oop8.model

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.utils.tree.Observable
import ru.brightos.oop8.view.SelectableView

class StickyShape(val shape: CShape) : Observable<CShape> {
    private val observers = arrayListOf<(CShape) -> Unit>()

    override fun notifyObservers() {
        observers.forEach {
            it(shape)
        }
    }

    override fun registerObserver(observer: (CShape) -> Unit) {
        observers.add(observer)
    }

    override fun deleteAllObservers() {
        observers.clear()
    }
}