package ru.brightos.oop8.model

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import org.json.JSONArray
import org.json.JSONObject
import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.utils.extendedListOf
import ru.brightos.oop8.utils.forEach

class CGroup(val shapesList: ExtendedList<CShape>) : CShape(
    fromX = shapesList.minOf { it.fromX },
    fromY = shapesList.minOf { it.fromY },
    toX = shapesList.maxOf { it.toX },
    toY = shapesList.maxOf { it.toY }
) {

    override val typeName: String = CGroup.typeName

    override val fromX: Float
        get() = shapesList.minOf { it.fromX }
    override val fromY: Float
        get() = shapesList.minOf { it.fromY }
    override val toX: Float
        get() = shapesList.maxOf { it.toX }
    override val toY: Float
        get() = shapesList.maxOf { it.toY }

    override val path: Path
        get() = Path().apply {
            shapesList.forEach {
                addPath(it.path)
            }
            close()
        }

    override val selectPath: Path
        get() = Path().apply {
            shapesList.forEach {
                addPath(it.selectPath)
            }
            close()
        }

    override var color: Int = 0
        get() = shapesList[0].color
        set(value) {
            if (value != shapesList[0].color)
                shapesList.forEach {
                    it.color = value
                }
            field = value
        }

    override fun draw(canvas: Canvas, offsetX: Float, offsetY: Float) {
        shapesList.forEachIndexed { index, it ->
            it.draw(
                canvas,
                offsetX = it.fromX - fromX + offsetX,
                offsetY = it.fromY - fromY + offsetY
            )
        }
    }

    override fun drawSelected(canvas: Canvas, offsetX: Float, offsetY: Float) {
        shapesList.forEach {
            it.drawSelected(
                canvas,
                offsetX = it.fromX - fromX + offsetX,
                offsetY = it.fromY - fromY + offsetY
            )
        }
    }

    override fun getPath(offsetX: Float, offsetY: Float) =
        Path().apply {
            shapesList.forEachIndexed { index, it ->
                addPath(
                    it.getPath(
                        offsetX = it.fromX - fromX + offsetX,
                        offsetY = it.fromY - fromY + offsetY
                    )
                )
                close()
            }
        }

    override fun move(moveCommand: MoveCommand) {
        shapesList.forEach {
            it.move(moveCommand)
        }
        super.move(moveCommand)
    }

    override fun onShapeResized(value: Float) {
        shapesList.forEach {
            it.onShapeResized(value)
        }

        val fromX = shapesList.minOf { it.fromX }
        val fromY = shapesList.minOf { it.fromY }
        val toX = shapesList.maxOf { it.toX }
        val toY = shapesList.maxOf { it.toY }

        this.centerX = (fromX + toX) / 2
        this.centerY = (fromY + toY) / 2
        this.width = toX - fromX
        this.height = toY - fromY
    }

    override fun couldBeResized(newWidth: Float, parentBounds: RectF): Boolean {
        var couldBeResized = true
        shapesList.forEach {
            couldBeResized = couldBeResized && it.couldBeResized(newWidth, parentBounds)
        }
        return couldBeResized
    }
    
    override fun couldBeMoved(moveCommand: MoveCommand, parentBounds: RectF): Boolean {
        var couldBeMoved = true
        shapesList.forEach {
            couldBeMoved = couldBeMoved && it.couldBeMoved(moveCommand, parentBounds)
        }
        return couldBeMoved
    }

    override fun isIntersected(other: CShape): Boolean {
        var isIntersected = false
        shapesList.forEach {
            isIntersected = isIntersected || it.isIntersected(other)
        }
        return isIntersected
    }

    override fun save() = JSONObject().apply {
        put(type, typeName)
        put(content, JSONArray().apply {
            shapesList.forEach {
                put(it.save())
            }
        })
    }

    companion object {
        const val content = "content"
        const val typeName = "group"

        fun load(jsonObject: JSONObject, factory: ShapeLoadFactory): CGroup {
            val shapesList = extendedListOf<CShape>()
            val content = jsonObject.getJSONArray("content")
            content.forEach {
                shapesList.add(factory.createShape(it))
            }
            return CGroup(shapesList)
        }
    }

}