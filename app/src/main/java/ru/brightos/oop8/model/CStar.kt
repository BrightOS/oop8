package ru.brightos.oop8.model

import android.graphics.Path
import android.graphics.Path.FillType
import org.json.JSONObject
import ru.brightos.oop8.view.SelectableView
import kotlin.math.cos
import kotlin.math.sin


class CStar(
    centerX: Float = 0f,
    centerY: Float = 0f,
    var borderLength: Float = 0f
) : CShape(
    centerX = centerX,
    centerY = centerY,
    width = borderLength
) {

    override val typeName: String = CStar.typeName

    override val path: Path
        get() = getStarPath(0f)

    override val selectPath: Path
        get() = getStarPath(SelectableView.selectedStrokeWidth.div(2))

    override fun onShapeResized(value: Float) {
        super.onShapeResized(value)
       borderLength = value
    }

    private fun getStarPath(margin: Float): Path {
        val halfWidth = borderLength / 2f
        val radius = halfWidth / 2f
        val degreesPerStep = Math.toRadians(360.0 / 5)
        val halfDegreesPerStep = degreesPerStep / 2f

        return Path().apply {
            fillType = FillType.EVEN_ODD
            val max = (2.0f * Math.PI).toFloat()
            moveTo(borderLength, halfWidth)
            var step = 0.0
            while (step < max) {
                lineTo(
                    (halfWidth + (halfWidth - margin) * cos(step)).toFloat(),
                    (halfWidth + (halfWidth - margin) * sin(step)).toFloat()
                )
                lineTo(
                    (halfWidth + (radius - margin) * cos(step + halfDegreesPerStep)).toFloat(),
                    (halfWidth + (radius - margin) * sin(step + halfDegreesPerStep)).toFloat()
                )
                step += degreesPerStep
            }
            close()
        }
    }

    override fun save() = JSONObject().apply {
        put(type, typeName)
        put(CShape.color, super.color)
        put(Companion.centerX, centerX.toDouble())
        put(Companion.centerY, centerY.toDouble())
        put(Companion.borderLength, borderLength.toDouble())
    }

    companion object {
        const val typeName = "star"
        const val centerX = "x"
        const val centerY = "y"
        const val borderLength = "sideLength"

        fun load(jsonObject: JSONObject) =
            CStar(
                centerX = jsonObject.getDouble(centerX).toFloat(),
                centerY = jsonObject.getDouble(centerY).toFloat(),
                borderLength = jsonObject.getDouble(borderLength).toFloat()
            ).apply {
                color = jsonObject.getInt(CShape.color)
            }
    }
}