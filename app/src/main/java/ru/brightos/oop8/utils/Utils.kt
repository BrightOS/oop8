package ru.brightos.oop8.utils

import android.content.res.Resources
import android.graphics.Path
import android.graphics.PointF
import android.util.TypedValue
import org.json.JSONArray
import org.json.JSONObject
import ru.brightos.oop8.data.ExtendedList

fun <T> extendedListOf(vararg elements: T): ExtendedList<T> =
    if (elements.size == 0) ExtendedList() else ExtendedList(elements.toList())

val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    )

val Number.px
    get() = (this.toFloat() / Resources.getSystem().displayMetrics.density)

fun correctDeclensionOfObjects(elementsCount: Int) =
    if (elementsCount % 100 / 10 != 1) when (elementsCount) {
        1 -> "объект"
        in 2..4 -> "объекта"
        else -> "объектов"
    }
    else "объектов"

fun correctDeclensionOfDeleted(deletedCount: Int) =
    if (deletedCount % 100 / 10 != 1 && deletedCount % 10 == 1)
        "Удалён"
    else
        "Удалено"

fun createPathBasedOnPoints(points: List<PointF>) =
    Path().apply {
        moveTo(points[0].x, points[0].y)
        repeat(points.size - 1) {
            lineTo(points[it + 1].x, points[it + 1].y)
        }
        close()
    }

fun JSONArray.forEach(action: (element: JSONObject) -> Unit) {
    repeat(length()) {
        action(getJSONObject(it))
    }
}