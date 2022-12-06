package ru.brightos.oop8.model

import org.json.JSONObject

class ShapeLoadFactoryImpl : ShapeLoadFactory {
    override fun createShape(shapeJsonObject: JSONObject): CShape =
        when (shapeJsonObject.getString(CShape.type)) {
            CCircle.typeName -> CCircle.load(shapeJsonObject)
            CSquare.typeName -> CSquare.load(shapeJsonObject)
            CStar.typeName -> CStar.load(shapeJsonObject)
            CTriangle.typeName -> CTriangle.load(shapeJsonObject)
            else -> CGroup.load(shapeJsonObject, this)
        }
}