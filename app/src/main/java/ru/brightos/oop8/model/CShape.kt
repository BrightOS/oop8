package ru.brightos.oop8.model

import android.graphics.*
import androidx.core.graphics.ColorUtils
import org.json.JSONObject
import ru.brightos.oop8.view.SelectableView

var lastID = 0L

abstract class CShape {

    val stickyShape = StickyShape()

    val id = lastID++
    var lastOperationID = -1L

    var centerX: Float
    var centerY: Float
    var height: Float
    var width: Float
    var selected: Boolean = false

    abstract val typeName: String
    abstract val path: Path
    abstract val selectPath: Path

    open var color: Int = Color.parseColor("#3E94D1")

    constructor(centerX: Float = 0f, centerY: Float = 0f, width: Float = 0f) {
        this.centerX = centerX
        this.centerY = centerY
        this.width = width
        this.height = width
    }

    constructor(fromX: Float, fromY: Float, toX: Float, toY: Float) {
        this.centerX = (fromX + toX) / 2
        this.centerY = (fromY + toY) / 2
        this.width = toX - fromX
        this.height = toY - fromY
    }

    open val fromX: Float
        get() = centerX - width / 2
    open val fromY: Float
        get() = centerY - height / 2
    open val toX: Float
        get() = centerX + width / 2
    open val toY: Float
        get() = centerY + height / 2

    open fun draw(canvas: Canvas, offsetX: Float = 0f, offsetY: Float = 0f) {
        canvas.drawPath(
            getPath(offsetX, offsetY),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = this@CShape.color
            }
        )
    }

    open fun getPath(offsetX: Float = 0f, offsetY: Float = 0f) =
        path.apply { offset(offsetX, offsetY) }

    open fun isTapInPath(x: Int, y: Int, offsetX: Float = 0f, offsetY: Float = 0f) =
        Region().apply {
            setPath(
                getPath(offsetX, offsetY),
                Region(
                    0,
                    0,
                    width.toInt(),
                    height.toInt()
                )
            )
        }.contains(x, y)

    open fun drawSelected(canvas: Canvas, offsetX: Float = 0f, offsetY: Float = 0f) {
        canvas.drawPath(
            selectPath.apply { offset(offsetX, offsetY) },
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = SelectableView.selectedStrokeWidth
                color = ColorUtils.blendARGB(this@CShape.color, Color.parseColor("#000000"), 0.3f)
            }
        )
    }

    open fun onShapeResized(value: Float) {
        width = value
        height = value
    }

    fun resize(newWidth: Float) {
        onShapeResized(newWidth)
    }

    open fun couldBeResized(newWidth: Float, parentBounds: RectF) =
        (newWidth / 2).let {
            (centerX - it >= parentBounds.left
                && centerY - it >= parentBounds.top
                && (it * width / height).let {
                (centerX + it <= parentBounds.right
                    && centerY + it <= parentBounds.bottom)
            })
        }

    open fun couldBeMoved(moveCommand: MoveCommand, parentBounds: RectF) =
        moveCommand.let {
            return@let fromX + it.deltaX in parentBounds.left..parentBounds.right
                && toX + it.deltaX in parentBounds.left..parentBounds.right
                && fromY + it.deltaY in parentBounds.top..parentBounds.bottom
                && toY + it.deltaY in parentBounds.top..parentBounds.bottom
        }

    open fun move(moveCommand: MoveCommand) {
        if (moveCommand.id != lastOperationID) {
            lastOperationID = moveCommand.id

            centerX += moveCommand.deltaX
            centerY += moveCommand.deltaY

            stickyShape.onMoveProceed(moveCommand)
        }
    }

    abstract fun save(): JSONObject

    companion object {
        const val type = "type"
        const val color = "color"
    }
}