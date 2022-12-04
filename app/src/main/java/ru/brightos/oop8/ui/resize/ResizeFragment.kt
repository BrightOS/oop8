package ru.brightos.oop8.ui.resize

import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import ru.brightos.oop8.data.PreferenceRepository
import ru.brightos.oop8.databinding.FragmentResizeBinding
import javax.inject.Inject

@AndroidEntryPoint
class ResizeFragment : Fragment() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    lateinit var binding: FragmentResizeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resizeSlider.apply {
            valueFrom = 30f
            valueTo = 120f
            addOnChangeListener { slider, value, fromUser ->
                if (fromUser)
                    preferenceRepository.selectedSize = value
            }
        }
        preferenceRepository.selectedSizeLive.observe(viewLifecycleOwner) {
            binding.resizeSlider.value = it
        }
    }
}