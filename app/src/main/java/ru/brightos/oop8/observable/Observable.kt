package ru.brightos.oop8.observable

interface Observable<E> {
    fun notifyObservers()
    fun registerObserver(id: Long = 0L, observer: (E) -> Unit)
    fun deleteAllObservers()
}