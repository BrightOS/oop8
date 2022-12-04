package ru.brightos.oop8.model

import android.graphics.Path
import android.graphics.PointF
import org.json.JSONObject
import ru.brightos.oop8.utils.createPathBasedOnPoints
import ru.brightos.oop8.view.SelectableView

class CTriangle(
    centerX: Float = 0f,
    centerY: Float = 0f,
    var bottomSideLength: Float = 0f
) : CShape(
    centerX = centerX,
    centerY = centerY,
    width = bottomSideLength
) {

    override val typeName: String = CTriangle.typeName

    override val path: Path
        get() = createPathBasedOnPoints(
            listOf(
                PointF(bottomSideLength / 2, 0f),
                PointF(bottomSideLength, bottomSideLength),
                PointF(0f, bottomSideLength)
            )
        )

    override val selectPath: Path
        get() = SelectableView.selectedStrokeWidth.div(2).let {
            return createPathBasedOnPoints(
                listOf(
                    PointF(bottomSideLength / 2, it),
                    PointF(bottomSideLength - it, bottomSideLength - it),
                    PointF(it, bottomSideLength - it)
                )
            )
        }

    override fun onShapeResized(value: Float) {
        super.onShapeResized(value)
        bottomSideLength = value
    }

    override fun save() = JSONObject().apply {
        put(type, typeName)
        put(CShape.color, super.color)
        put(Companion.centerX, centerX.toDouble())
        put(Companion.centerY, centerY.toDouble())
        put(Companion.bottomSideLength, bottomSideLength.toDouble())
    }

    companion object {
        const val typeName = "triangle"
        const val centerX = "x"
        const val centerY = "y"
        const val bottomSideLength = "bottomSideLength"

        fun load(jsonObject: JSONObject) =
            CTriangle(
                centerX = jsonObject.getDouble(centerX).toFloat(),
                centerY = jsonObject.getDouble(centerY).toFloat(),
                bottomSideLength = jsonObject.getDouble(bottomSideLength).toFloat()
            ).apply {
                color = jsonObject.getInt(CShape.color)
            }
    }
}