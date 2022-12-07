package ru.brightos.oop8.observable

import ru.brightos.oop8.model.MoveCommand

class StickyObservable : Observable<MoveCommand> {
    private val observers = arrayListOf<(MoveCommand) -> Unit>()
    private val shapesIDs = arrayListOf<Long>()

    var currentDeltaCoordinates = MoveCommand(0, 0, -1)

    fun isSticked(id: Long) = shapesIDs.contains(id)

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
        if (shapesIDs.contains(id).not()) {
            observers.add(observer)
            shapesIDs.add(id)
        }
    }

    override fun deleteAllObservers() {
        observers.clear()
        shapesIDs.clear()
    }
}