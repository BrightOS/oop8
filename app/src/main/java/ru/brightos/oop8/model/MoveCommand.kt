package ru.brightos.oop8.model

var operationID = 0L

class MoveCommand(
    val deltaX: Int,
    val deltaY: Int,
    val initializedShapeID: Long
) {
    val id = operationID++
}