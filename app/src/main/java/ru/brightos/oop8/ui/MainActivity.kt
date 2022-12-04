package ru.brightos.oop8.ui

import android.annotation.SuppressLint
import android.graphics.RectF
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import ru.brightos.oop8.R
import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.data.PreferenceRepository
import ru.brightos.oop8.databinding.ActivityMainBinding
import ru.brightos.oop8.model.*
import ru.brightos.oop8.utils.*
import ru.brightos.oop8.utils.edit.OnDeleteClickedListener
import ru.brightos.oop8.utils.edit.OnGroupClickedListener
import ru.brightos.oop8.utils.move.MoveKeyEvent
import ru.brightos.oop8.utils.move.OnMoveKeyPressedListener
import ru.brightos.oop8.utils.tree.TreeObservable
import ru.brightos.oop8.view.OnItemSelectListener
import ru.brightos.oop8.view.SelectableView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import kotlin.io.path.createDirectory

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMoveKeyPressedListener, OnDeleteClickedListener,
    OnGroupClickedListener {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private lateinit var binding: ActivityMainBinding

    private val onItemSelectListener = object : OnItemSelectListener {
        override fun deselectAllObjects() {
            shapesList.forEach { it.deselect() }
        }

        override fun onItemSelected(shape: CShape) {
            TreeObservable.instance.notifyObservers()
            println("selected")
        }
    }

    private val shapesList = extendedListOf<SelectableView>()
    private var parentBounds = RectF()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TreeObservable.newInstance(shapesList)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFromFile()

        initializeParentBounds()
        prepareEditorGUI()

        binding.drawLayout.setOnTouchAction { motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {

                val modifiedSize = preferenceRepository.selectedSize.dp

                val x = motionEvent.x.let {
                    if (it < modifiedSize)
                        modifiedSize / 2
                    else if (it > parentBounds.right - parentBounds.left - modifiedSize / 2)
                        parentBounds.right - parentBounds.left - modifiedSize / 2
                    else
                        it
                }

                val y = motionEvent.y.let {
                    if (it < modifiedSize / 2)
                        modifiedSize / 2
                    else if (it > parentBounds.bottom - parentBounds.top - modifiedSize / 2)
                        parentBounds.bottom - parentBounds.top - modifiedSize / 2
                    else
                        it
                }

                val newShape = SelectableView(
                    context = this,
                    attrs = null,
                    shape = ShapeDefaultFactory.createDefaultShape(
                        x = x,
                        y = y,
                        borderSize = modifiedSize,
                        selectedShapeType = preferenceRepository.selectedShape
                    ),
                    onSingleObjectSelectedListener = onItemSelectListener
                ).apply {
                    fillColor = preferenceRepository.selectedColor
                }

                shapesList.add(newShape)
                binding.drawLayout.addView(newShape)
                TreeObservable.instance.notifyObservers()
            }
        }

        preferenceRepository.selectedColorLive.observe(this) { value ->
            shapesList.forEach {
                if (it.isSelected)
                    it.fillColor = value
            }
        }

        preferenceRepository.selectedSizeLive.observe(this) { value ->
            val dpValue = value.dp
            var couldBeResized = true

            shapesList.forEach {
                if (it.isSelected)
                    couldBeResized = couldBeResized && it.couldBeResized(dpValue, parentBounds)
            }

            if (couldBeResized)
                shapesList.forEach {
                    if (it.isSelected)
                        it.resize(dpValue)
                }
        }

        TreeObservable.instance.registerObserver {
            shapesList.forEach {
                it.invalidate()
            }
        }
    }

    /**
     * Preparing adapter for tabs and syncing tabs with viewpager
     */
    private fun prepareEditorGUI() {
        binding.settingsViewPager.adapter = SettingsPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle
        )

        TabLayoutMediator(binding.settingsTabs, binding.settingsViewPager) { tab, position ->
            when (position) {
                0 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_more_horiz)
                1 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_aspect_ratio)
                2 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_move)
                3 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_palette)
                4 -> tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_tree)
            }
        }.attach()
    }

    /**
     * Getting parent bounds size when it is possible
     */
    private fun initializeParentBounds() {
        val viewTreeObserver = binding.drawLayout.viewTreeObserver

        if (viewTreeObserver.isAlive)
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.drawLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.drawLayout.let {
                        parentBounds = RectF(
                            it.left.toFloat(), it.top.toFloat(),
                            it.right.toFloat(), it.bottom.toFloat()
                        )
                        println("parent $parentBounds")
                    }
                }
            })
    }

    /**
     * Performing an action when single move key was pressed
     */
    override fun onMoveKeyPressed(moveKeyEvent: MoveKeyEvent) {
        val delta = 2.dp.toInt()

        when (moveKeyEvent) {
            MoveKeyEvent.LEFT -> {
                var minLeft = parentBounds.right
                shapesList.forEach {
                    if (it.isSelected && it.shape.fromX < minLeft) minLeft = it.shape.fromX
                }

                println("left ${minLeft - delta} ${parentBounds.left}")

                if (minLeft - delta >= parentBounds.left) {
                    shapesList.forEach {
                        if (it.isSelected)
                            it.move(-delta, 0)
                    }
                }
            }

            MoveKeyEvent.RIGHT -> {
                var maxRight = parentBounds.left
                shapesList.forEach {
                    if (it.isSelected && it.shape.toX > maxRight) maxRight = it.shape.toX
                }

                if (maxRight + delta <= parentBounds.right) {
                    shapesList.forEach {
                        if (it.isSelected)
                            it.move(delta, 0)
                    }
                }
            }

            MoveKeyEvent.UP -> {
                var minTop = parentBounds.bottom
                shapesList.forEach {
                    if (it.isSelected && it.shape.fromY < minTop) minTop = it.shape.fromY
                }

                if (minTop - delta >= parentBounds.top) {
                    shapesList.forEach {
                        if (it.isSelected)
                            it.move(0, -delta)
                    }
                }
            }

            MoveKeyEvent.DOWN -> {
                var maxBottom = parentBounds.top
                shapesList.forEach {
                    if (it.isSelected && it.shape.toY > maxBottom) maxBottom = it.shape.toY
                }

                if (maxBottom + delta <= parentBounds.bottom) {
                    shapesList.forEach {
                        if (it.isSelected)
                            it.move(0, delta)
                    }
                }
            }
        }
    }

    /**
     * Deleting all of the selected elements
     * @return deleted objects count
     */
    private fun deleteSelected(): Int {
        val idsToDelete = extendedListOf<Int>()

        shapesList.forEachIndexed { index, element ->
            if (element.deleteView())
                idsToDelete.add(index)
        }

        val deletedObjectsCount = idsToDelete.size

        while (!idsToDelete.isEmpty)
            shapesList.removeAt(idsToDelete.popLast()!!)

        TreeObservable.instance.notifyObservers()
        return deletedObjectsCount
    }

    override fun onDeleteClicked() {
        val deletedObjectsCount = deleteSelected()

        Snackbar.make(
            binding.drawLayout,
            "${
                correctDeclensionOfDeleted(deletedObjectsCount)
            } $deletedObjectsCount ${
                correctDeclensionOfObjects(deletedObjectsCount)
            }", // Удален(о) N объект(а)(ов)
            Snackbar.LENGTH_LONG
        )
            .setAction("Ок") {}
            .setAnchorView(binding.settingsTabs)
            .show()
    }

    override fun onDeleteAllClicked() {
        shapesList.forEach {
            it.select()
        }
        onDeleteClicked()
    }

    override fun onPause() {
        saveToFile()
        super.onPause()
    }

    /**
     * Reading JSON string from $fileName file
     * in $directoryAbsolutePath folder and parsing it
     */
    private fun loadFromFile() {
        val file = File(
            directoryAbsolutePath,
            fileName
        )

        if (file.exists())
            JSONObject(
                InputStreamReader(
                    FileInputStream(
                        file
                    )
                ).readText()
            ).getJSONArray(objects).forEach {
                SelectableView(
                    context = this,
                    attrs = null,
                    shape = ShapeLoadFactory.createShape(it),
                    onSingleObjectSelectedListener = onItemSelectListener
                ).let { view ->
                    view.isSelected = false
                    shapesList.add(view)
                    binding.drawLayout.addView(view)
                    TreeObservable.instance.notifyObservers()
                }
            }
    }

    /**
     * Saving objects to JSON string to $fileName
     * file in $directoryAbsolutePath folder
     */
    private fun saveToFile() {
        val shapesArray = JSONArray().apply {
            shapesList.forEach {
                put(it.shape.save())
            }
        }
        println(shapesArray)

        val resultObject = JSONObject().apply {
            put(count, shapesList.size)
            put(objects, shapesArray)
        }

        val file = File(directoryAbsolutePath, fileName)

        File(directoryAbsolutePath).let {
            if (it.exists().not())
                it.toPath().createDirectory()
        }

        if (file.exists().not())
            file.createNewFile()
        OutputStreamWriter(FileOutputStream(file))
            .apply {
                write(resultObject.toString(2))
                flush()
                close()
            }
        println("saved")
    }

    companion object {
        private const val count = "count"
        private const val documentsFolderName = "oop8"
        private const val fileName = "state.txt"
        private const val objects = "objects"

        private val directoryAbsolutePath =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}/$documentsFolderName"
    }

    override fun onGroupClicked() {
        val shapesForGrouping = extendedListOf<CShape>()

        shapesList.forEach {
            if (it.isSelected)
                shapesForGrouping.add(it.shape)
        }

        deleteSelected()

        SelectableView(
            context = this,
            attrs = null,
            CGroup(shapesForGrouping),
            onItemSelectListener
        ).let {
            shapesList.add(it)
            binding.drawLayout.addView(it)
            TreeObservable.instance.notifyObservers()
        }
    }

    override fun onUngroupClicked() {
        val idsToDelete = extendedListOf<Int>()

        shapesList.forEachIndexed { index, element ->
            if (element.isSelected && element.shape is CGroup) {
                (element.shape as CGroup).shapesList.forEach { shape ->
                    SelectableView(
                        context = this,
                        attrs = null,
                        shape = shape,
                        onSingleObjectSelectedListener = onItemSelectListener
                    ).let {
                        binding.drawLayout.addView(it)
                        shapesList.add(it)
                    }
                }

                binding.drawLayout.removeView(element)
                idsToDelete.add(index)
            }
        }

        println(idsToDelete)

        while (!idsToDelete.isEmpty)
            shapesList.removeAt(idsToDelete.popLast()!!)

        TreeObservable.instance.notifyObservers()
    }

}