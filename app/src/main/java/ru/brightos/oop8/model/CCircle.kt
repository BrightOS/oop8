package ru.brightos.oop8.model

import android.graphics.Path
import org.json.JSONObject
import ru.brightos.oop8.view.SelectableView


class CCircle(
    centerX: Float,
    centerY: Float,
    var radius: Float
) : CShape(
    centerX = centerX,
    centerY = centerY,
    width = radius * 2
) {

    override val typeName: String = CCircle.typeName

    override val path: Path
        get() = Path().apply {
            addCircle(radius, radius, radius, Path.Direction.CCW)
        }

    override val selectPath: Path
        get() = Path().apply {
            addCircle(
                radius,
                radius,
                radius - SelectableView.selectedStrokeWidth.div(2),
                Path.Direction.CCW
            )
        }

    override fun onShapeResized(value: Float) {
        super.onShapeResized(value)
        radius = value / 2
    }

    override fun save() = JSONObject().apply {
        put(type, typeName)
        put(CShape.color, super.color)
        put(Companion.centerX, centerX.toDouble())
        put(Companion.centerY, centerY.toDouble())
        put(Companion.radius, radius.toDouble())
    }

    companion object {
        const val typeName = "circle"
        const val centerX = "x"
        const val centerY = "y"
        const val radius = "radius"

        fun load(jsonObject: JSONObject) =
            CCircle(
                centerX = jsonObject.getDouble(centerX).toFloat(),
                centerY = jsonObject.getDouble(centerY).toFloat(),
                radius = jsonObject.getDouble(radius).toFloat()
            ).apply {
                color = jsonObject.getInt(CShape.color)
            }
    }

}