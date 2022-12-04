package ru.brightos.oop8.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.coordinatorlayout.widget.CoordinatorLayout

class DrawLayout : CoordinatorLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet?) : super(context, attr)
    constructor(context: Context, attr: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!super.dispatchTouchEvent(ev))
            onTouchAction(ev)
        return super.dispatchTouchEvent(ev)
    }

    private var onTouchAction: (MotionEvent) -> Unit = {}

    fun setOnTouchAction(l: (MotionEvent) -> Unit) {
        onTouchAction = l
    }

    class LayoutParams(
        width: Int,
        height: Int,
        marginLeft: Int,
        marginTop: Int
    ) : CoordinatorLayout.LayoutParams(width, height) {
        init {
            setMargins(marginLeft, marginTop, 0, 0)
        }
    }
}