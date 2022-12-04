package ru.brightos.oop8.ui

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flask.colorpicker.OnColorChangedListener
import dagger.hilt.android.AndroidEntryPoint
import ru.brightos.oop8.data.PreferenceRepository
import ru.brightos.oop8.databinding.FragmentColorPickerBinding
import javax.inject.Inject

@AndroidEntryPoint
class ColorPickerFragment : Fragment() {

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private lateinit var binding: FragmentColorPickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.colorPickerView.setColor(preferenceRepository.selectedColor, false)
        binding.colorPickerView.addOnColorChangedListener {
            preferenceRepository.selectedColor = it
        }
    }
}