package ru.brightos.oop8.view

import ru.brightos.oop8.model.CShape

interface OnItemSelectListener {
    fun deselectAllObjects()
    fun onItemSelected(shape: CShape)
}