package ru.brightos.oop8.utils.tree

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.model.StickyShape
import ru.brightos.oop8.utils.extendedListOf
import ru.brightos.oop8.view.SelectableView

class TreeObservable(
    private val list: ExtendedList<StickyShape>
) : Observable<ExtendedList<StickyShape>> {
    private val observers = arrayListOf<(ExtendedList<StickyShape>) -> Unit>()

    override fun notifyObservers() {
        observers.forEach {
            it(list)
        }
    }

    override fun registerObserver(observer: (ExtendedList<StickyShape>) -> Unit) {
        observers.add(observer)
    }

    override fun deleteAllObservers() {
        observers.clear()
    }

    companion object {
        lateinit var instance: TreeObservable

        fun newInstance(list: ExtendedList<SelectableView>): TreeObservable {
            val newList = extendedListOf<StickyShape>()
            list.forEach {
                newList.add(StickyShape(it.shape))
            }
            if (this::instance.isInitialized.not())
                instance = TreeObservable(newList)
            return instance
        }

        fun setStickyChildren(ss: StickyShape, ) {
        }
    }
}