package ru.brightos.oop8.utils.move

interface OnMoveKeyPressedListener : java.io.Serializable {
    fun onMoveKeyPressed(moveKeyEvent: MoveKeyEvent)
}