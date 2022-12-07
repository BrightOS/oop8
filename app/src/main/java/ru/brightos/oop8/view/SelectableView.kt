package ru.brightos.oop8.view

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.core.util.toRange
import ru.brightos.oop8.R
import ru.brightos.oop8.model.*
import ru.brightos.oop8.utils.dp
import ru.brightos.oop8.utils.edit.ShapeType
import ru.brightos.oop8.utils.overlap


open class SelectableView : View {

    companion object {
        val selectedStrokeWidth = 5.dp
        private const val LONG_PRESS_DELAY = 50
    }

    override fun isSelected(): Boolean {
        return shape.selected
    }

    override fun setSelected(selected: Boolean) {
        shape.selected = selected
    }

    var longPressEnabled = true

    private var onItemSelectListener: OnItemSelectListener? = null
    var shape: CShape = CCircle(0f, 0f, 0f)

    fun setOnItemSelectListener(onItemSelectListener: OnItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener
    }

    val bounds
        get() = RectF(
            shape.fromX,
            shape.fromY,
            shape.toX,
            shape.toY
        )

    fun isTapInRegion(x: Int, y: Int) = shape.isTapInPath(x, y)

    fun isIntersected(other: SelectableView) =
        shape.isIntersected(other.shape)

    var fillColor: Int
        get() = shape.color
        set(value) {
            shape.color = value
            invalidate()
        }

    constructor(context: Context, attrs: AttributeSet? = null) : super(
        context,
        attrs
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SelectableView, 0, 0)

        val shapeType = a.getInt(R.styleable.SelectableView_shapeType, 1)

        val defaultSize = 50.dp

        this.shape = ShapeDefaultFactory.createDefaultShape(
            defaultSize,
            defaultSize,
            defaultSize,
            mapOf(
                1 to ShapeType.CIRCLE,
                2 to ShapeType.SQUARE,
                3 to ShapeType.TRIANGLE
            )[shapeType] ?: ShapeType.STAR
        ).apply {
            deselect()
        }
        a.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(
            changed, bounds.left.toInt(), bounds.top.toInt(),
            bounds.right.toInt(), bounds.bottom.toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(shape.width.toInt(), shape.height.toInt())
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        shape: CShape,
        onSingleObjectSelectedListener: OnItemSelectListener
    ) : super(context, attrs) {
        shape.let {
            this.shape = it
            layoutParams = generateLayoutParams()
        }
        fillColor = shape.color
        this.onItemSelectListener = onSingleObjectSelectedListener
        selectOnlyThis()
    }

    override fun onDraw(canvas: Canvas) {
        shape.draw(canvas)
        if (isSelected)
            shape.drawSelected(canvas)
        super.onDraw(canvas)
    }

    private var boundaries: Rect? = null
    private var mHandler = Handler(Looper.myLooper()!!)
    private var longClickPerformed = false

    private var onTap = Runnable {
        handler.postDelayed(
            onLongPress,
            LONG_PRESS_DELAY - ViewConfiguration.getTapTimeout().toLong()
        )
    }

    private var onLongPress = Runnable {
        longClickPerformed = true
        select()
        performLongClick()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                longClickPerformed = false
                if (isTapInRegion(event.x.toInt(), event.y.toInt())) {
                    boundaries = Rect(left, top, right, bottom)
                    if (longPressEnabled)
                        mHandler.postDelayed(onTap, ViewConfiguration.getTapTimeout().toLong())
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mHandler.removeCallbacks(onLongPress)
                mHandler.removeCallbacks(onTap)
            }
            MotionEvent.ACTION_UP -> {
                mHandler.removeCallbacks(onLongPress)
                mHandler.removeCallbacks(onTap)
                if (!longClickPerformed || !longPressEnabled) {
                    selectOnlyThis()
                    performClick()
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isTapInRegion(
                        event.x.toInt(),
                        event.y.toInt()
                    )
                ) {
                    mHandler.removeCallbacks(onLongPress)
                    mHandler.removeCallbacks(onTap)
                }
                return true
            }
        }
        return false
    }

    fun deleteView(): Boolean {
        if (isSelected)
            (parent as ViewGroup).removeView(this)
        return isSelected
    }

    fun select() {
        isSelected = true
        invalidate()
        onItemSelectListener?.onItemSelected(shape)
    }

    fun deselect() {
        isSelected = false
        invalidate()
    }

    fun selectOnlyThis() {
        onItemSelectListener?.deselectAllObjects()
        isSelected = true
        invalidate()
        onItemSelectListener?.onItemSelected(shape)
    }

    fun couldBeMoved(moveCommand: MoveCommand, parentBounds: RectF) =
        shape.couldBeMoved(moveCommand, parentBounds)

    fun move(moveCommand: MoveCommand) {
        shape.move(moveCommand)
        layoutParams = generateLayoutParams()
        requestLayout()
    }

    fun couldBeResized(newBorderLength: Float, parentBounds: RectF): Boolean =
        shape.couldBeResized(newBorderLength, parentBounds)

    fun resize(newBorderLength: Float) {
        shape.resize(newBorderLength)
        layoutParams = generateLayoutParams()
        requestLayout()
    }

    private fun generateLayoutParams() = DrawLayout.LayoutParams(
        width = shape.width.toInt(),
        height = shape.height.toInt(),
        marginLeft = shape.fromX.toInt(),
        marginTop = shape.fromY.toInt()
    )
}