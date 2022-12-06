package ru.brightos.oop8.model

import org.json.JSONObject

interface ShapeLoadFactory {
    fun createShape(shapeJsonObject: JSONObject): CShape
}