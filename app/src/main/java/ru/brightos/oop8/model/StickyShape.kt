package ru.brightos.oop8.model

import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.utils.tree.Observable
import ru.brightos.oop8.view.SelectableView

class StickyShape : Observable<MoveCommand> {
    private val observers = arrayListOf<(MoveCommand) -> Unit>()
    var currentDeltaCoordinates = MoveCommand(0, 0, false)

    fun onMoveProceed(moveCommand: MoveCommand) {
        currentDeltaCoordinates = moveCommand
        notifyObservers()
    }

    override fun notifyObservers() {
        observers.forEach {
            it(currentDeltaCoordinates)
        }
    }

    override fun registerObserver(observer: (MoveCommand) -> Unit) {
        observers.add(observer)
    }

    override fun deleteAllObservers() {
        observers.clear()
    }
}