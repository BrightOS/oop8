package ru.brightos.oop8.ui.move

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.brightos.oop8.databinding.FragmentMoveBinding
import ru.brightos.oop8.utils.move.MoveKeyEvent
import ru.brightos.oop8.utils.move.OnMoveKeyPressedListener

@AndroidEntryPoint
class MoveFragment : Fragment() {

    private lateinit var binding: FragmentMoveBinding
    lateinit var onMoveKeyPressedListener: OnMoveKeyPressedListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMoveKeyPressedListener)
            onMoveKeyPressedListener = context
        else
            throw Exception("Must implement OnMoveKeyPressedListener")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.left.setOnTouchListener(object : MoveKeyHoldListener() {
            override fun action() {
                onMoveKeyPressedListener.onMoveKeyPressed(MoveKeyEvent.LEFT)
            }
        })

        binding.right.setOnTouchListener(object : MoveKeyHoldListener() {
            override fun action() {
                onMoveKeyPressedListener.onMoveKeyPressed(MoveKeyEvent.RIGHT)
            }
        })

        binding.up.setOnTouchListener(object : MoveKeyHoldListener() {
            override fun action() {
                onMoveKeyPressedListener.onMoveKeyPressed(MoveKeyEvent.UP)
            }
        })

        binding.down.setOnTouchListener(object : MoveKeyHoldListener() {
            override fun action() {
                onMoveKeyPressedListener.onMoveKeyPressed(MoveKeyEvent.DOWN)
            }
        })

    }

    companion object {
        fun newInstance() = MoveFragment()
    }

    abstract inner class MoveKeyHoldListener : View.OnTouchListener{
        private var handler: Handler? = null

        abstract fun action()

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (handler != null) return true
                    handler = Handler(Looper.myLooper()!!)
                    handler!!.postDelayed(holdingAction, 0)
                }
                MotionEvent.ACTION_UP -> {
                    if (handler == null) return true
                    handler!!.removeCallbacks(holdingAction)
                    handler = null
                }
            }
            return true
        }

        private var holdingAction = object : Runnable {
            override fun run() {
                action()
                handler!!.postDelayed(this, 0)
            }
        }
    }
}