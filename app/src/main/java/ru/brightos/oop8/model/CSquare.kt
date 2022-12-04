package ru.brightos.oop8.model

import android.graphics.Path
import android.graphics.RectF
import org.json.JSONObject
import ru.brightos.oop8.view.SelectableView

class CSquare(
    centerX: Float = 0f,
    centerY: Float = 0f,
    var sideLength: Float = 0f
) : CShape(
    centerX = centerX,
    centerY = centerY,
    width = sideLength
) {

    override val typeName: String = CSquare.typeName

    private val rect: RectF
        get() = RectF(0f, 0f, sideLength, sideLength)

    private val selectRect: RectF
        get() = SelectableView.selectedStrokeWidth.div(2)
            .let { RectF(it, it, sideLength - it, sideLength - it) }

    override val path: Path
        get() = Path().apply {
            addRect(rect, Path.Direction.CCW)
        }

    override val selectPath: Path
        get() = Path().apply {
            addRect(selectRect, Path.Direction.CCW)
        }

    override fun onShapeResized(value: Float) {
        super.onShapeResized(value)
        sideLength = value
    }

    override fun save() = JSONObject().apply {
        put(type, typeName)
        put(CShape.color, super.color)
        put(Companion.centerX, centerX.toDouble())
        put(Companion.centerY, centerY.toDouble())
        put(Companion.sideLength, sideLength.toDouble())
    }

    companion object {
        const val typeName = "square"
        const val centerX = "x"
        const val centerY = "y"
        const val sideLength = "sideLength"

        fun load(jsonObject: JSONObject) =
            CSquare(
                centerX = jsonObject.getDouble(centerX).toFloat(),
                centerY = jsonObject.getDouble(centerY).toFloat(),
                sideLength = jsonObject.getDouble(sideLength).toFloat()
            ).apply {
                color = jsonObject.getInt(CShape.color)
            }
    }
}