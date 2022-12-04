package ru.brightos.oop8.data

import android.content.SharedPreferences
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.brightos.oop8.utils.dp
import ru.brightos.oop8.utils.edit.ShapeType

class PreferenceRepository(private val sharedPreferences: SharedPreferences) {

    // Color repository

    var selectedColor: Int
        get() = sharedPreferences.getInt(PREFERENCE_SELECTED_COLOR, Color.WHITE)
        set(value) {
            sharedPreferences.edit().putInt(PREFERENCE_SELECTED_COLOR, value).apply()
            _selectedColorLive.value = value
        }

    private val _selectedColorLive: MutableLiveData<Int> = MutableLiveData()
    val selectedColorLive: LiveData<Int>
        get() = _selectedColorLive

    // Shape repository

    var selectedShape: ShapeType
        get() = when (sharedPreferences.getInt(PREFERENCE_SELECTED_SHAPE, Color.WHITE)) {
            0 -> ShapeType.CIRCLE
            1 -> ShapeType.SQUARE
            2 -> ShapeType.TRIANGLE
            else -> ShapeType.STAR
        }
        set(value) {
            sharedPreferences.edit().putInt(PREFERENCE_SELECTED_SHAPE, when (value) {
                ShapeType.CIRCLE -> 0
                ShapeType.SQUARE -> 1
                ShapeType.TRIANGLE -> 2
                else -> 3
            }).apply()
            _selectedShapeLive.value = value
        }

    private val _selectedShapeLive: MutableLiveData<ShapeType> = MutableLiveData()
    val selectedShapeLive: LiveData<ShapeType>
        get() = _selectedShapeLive

    // Size repository

    var selectedSize: Float
        get() = sharedPreferences.getFloat(PREFERENCE_SELECTED_SIZE, 60f)
        set(value) {
            sharedPreferences.edit().putFloat(PREFERENCE_SELECTED_SIZE, value).apply()
            _selectedSizeLive.value = value
        }

    private val _selectedSizeLive: MutableLiveData<Float> = MutableLiveData()
    val selectedSizeLive: LiveData<Float>
        get() = _selectedSizeLive

    init {
        _selectedColorLive.value = selectedColor
        _selectedShapeLive.value = selectedShape
        _selectedSizeLive.value = selectedSize
    }

    companion object {
        private const val PREFERENCE_SELECTED_COLOR = "pref_selected_color"
        private const val PREFERENCE_SELECTED_SHAPE = "pref_selected_shape"
        private const val PREFERENCE_SELECTED_SIZE = "pref_selected_size"
    }
}