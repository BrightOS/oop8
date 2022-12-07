package ru.brightos.oop8.observable

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.view.SelectableView

class TreeObservable(
    private val list: ExtendedList<SelectableView>
) : Observable<ExtendedList<SelectableView>> {
    private val observers = arrayListOf<(ExtendedList<SelectableView>) -> Unit>()

    override fun notifyObservers() {
        observers.forEach {
            it(list)
        }
    }

    override fun registerObserver(id: Long, observer: (ExtendedList<SelectableView>) -> Unit) {
        observers.add(observer)
    }

    override fun deleteAllObservers() {
        observers.clear()
    }

    companion object {
        lateinit var instance: TreeObservable

        fun newInstance(list: ExtendedList<SelectableView>): TreeObservable {
            if (this::instance.isInitialized.not())
                instance = TreeObservable(list)
            return instance
        }
    }
}