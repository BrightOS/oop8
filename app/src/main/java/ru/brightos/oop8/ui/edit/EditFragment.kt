package ru.brightos.oop8.ui.edit

import android.content.ContextWrapper
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.brightos.oop8.data.ExtendedList
import ru.brightos.oop8.data.PreferenceRepository
import ru.brightos.oop8.databinding.FragmentEditBinding
import ru.brightos.oop8.model.CShape
import ru.brightos.oop8.utils.edit.OnDeleteClickedListener
import ru.brightos.oop8.utils.edit.OnGroupClickedListener
import ru.brightos.oop8.utils.edit.ShapeType
import ru.brightos.oop8.utils.extendedListOf
import ru.brightos.oop8.view.OnItemSelectListener
import ru.brightos.oop8.view.SelectableView
import javax.inject.Inject

@AndroidEntryPoint
class EditFragment : Fragment() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private lateinit var binding: FragmentEditBinding
    private lateinit var onDeleteClickedListener: OnDeleteClickedListener
    private lateinit var onGroupClickedListener: OnGroupClickedListener
    private lateinit var shapesList: ExtendedList<SelectableView>

    private val shapeTypesMap = mapOf(
        0 to ShapeType.CIRCLE,
        1 to ShapeType.SQUARE,
        2 to ShapeType.TRIANGLE,
        3 to ShapeType.STAR
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onDeleteClickedListener = (context as ContextWrapper).baseContext as OnDeleteClickedListener
        onGroupClickedListener = (context as ContextWrapper).baseContext as OnGroupClickedListener

        binding.delete.setOnClickListener { onDeleteClickedListener.onDeleteClicked() }
        binding.deleteAll.setOnClickListener { onDeleteClickedListener.onDeleteAllClicked() }
        binding.combine.setOnClickListener { onGroupClickedListener.onGroupClicked() }
        binding.ungroup.setOnClickListener { onGroupClickedListener.onUngroupClicked() }

        shapesList = extendedListOf(binding.circle, binding.square, binding.triangle, binding.star)
        val onItemSelectListener = object : OnItemSelectListener {
            override fun deselectAllObjects() {
                shapesList.forEach { it.deselect() }
            }

            override fun onItemSelected(shape: CShape) {}
        }
        shapesList.forEachIndexed { i, it ->
            it.setOnItemSelectListener(onItemSelectListener)
            it.longPressEnabled = false
            it.setOnClickListener { _ ->
                shapeTypesMap[i]?.let { preferenceRepository.selectedShape = it }
                println("Selected $i")
            }
        }

        preferenceRepository.selectedColorLive.observe(viewLifecycleOwner) { newColor ->
            shapesList.forEach {
                it.fillColor = newColor
            }
        }

        preferenceRepository.selectedShapeLive.observe(viewLifecycleOwner) {
            when (it) {
                ShapeType.CIRCLE -> shapesList[0].select()
                ShapeType.SQUARE -> shapesList[1].select()
                ShapeType.TRIANGLE -> shapesList[2].select()
                else -> shapesList[3].select()
            }
        }
    }

    companion object {
        fun newInstance() = EditFragment()
    }
}