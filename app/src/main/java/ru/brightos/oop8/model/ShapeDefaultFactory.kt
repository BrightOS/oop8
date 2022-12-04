package ru.brightos.oop8.model

import ru.brightos.oop8.utils.dp
import ru.brightos.oop8.utils.edit.ShapeType

object ShapeDefaultFactory {
    fun createDefaultShape(
        x: Float,
        y: Float,
        borderSize: Float,
        selectedShapeType: ShapeType
    ): CShape =
        when (selectedShapeType) {
            ShapeType.CIRCLE -> CCircle(
                centerX = x,
                centerY = y,
                radius = borderSize / 2
            )

            ShapeType.SQUARE -> CSquare(
                centerX = x,
                centerY = y,
                sideLength = borderSize
            )

            ShapeType.TRIANGLE -> CTriangle(
                centerX = x,
                centerY = y,
                bottomSideLength = borderSize
            )

            ShapeType.STAR -> CStar(
                centerX = x,
                centerY = y,
                borderLength = borderSize
            )
        }
}