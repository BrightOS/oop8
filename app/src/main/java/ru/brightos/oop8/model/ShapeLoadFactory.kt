package ru.brightos.oop8.model

import org.json.JSONObject

object ShapeLoadFactory {
    fun createShape(shapeJsonObject: JSONObject): CShape =
        when (shapeJsonObject.get("type")) {
            CCircle.typeName -> CCircle.load(shapeJsonObject)
            CSquare.typeName -> CSquare.load(shapeJsonObject)
            CStar.typeName -> CStar.load(shapeJsonObject)
            CTriangle.typeName -> CTriangle.load(shapeJsonObject)
            CGroup.typeName -> CGroup.load(shapeJsonObject)
            else -> throw Exception("No such shape type: ${shapeJsonObject.get("type")}")
        }
}