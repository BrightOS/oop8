package ru.brightos.oop8.model

import ru.brightos.oop8.utils.tree.Observable

class StickyShape : Observable<MoveCommand> {
    private val observers = arrayListOf<(MoveCommand) -> Unit>()
    private val observerIDs = arrayListOf<Long>()
    var currentDeltaCoordinates = MoveCommand(0, 0, -1)

    fun isSticked(id: Long) = observerIDs.contains(id)

    fun onMoveProceed(moveCommand: MoveCommand) {
        observers.forEach {
            it(moveCommand)
        }
    }

    override fun notifyObservers() {
        observers.forEach {
            it(currentDeltaCoordinates)
        }
    }

    override fun registerObserver(id: Long, observer: (MoveCommand) -> Unit) {
        if (observerIDs.contains(id).not()) {
            observers.add(observer)
            observerIDs.add(id)
        }
    }

    override fun deleteAllObservers() {
        observers.clear()
        observerIDs.clear()
    }
}