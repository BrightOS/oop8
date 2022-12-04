package ru.brightos.oop8.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.brightos.oop8.ui.edit.EditFragment
import ru.brightos.oop8.ui.move.MoveFragment
import ru.brightos.oop8.ui.resize.ResizeFragment
import ru.brightos.oop8.ui.tree.TreeFragment


class SettingsPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        val editFragment = EditFragment.newInstance()
        return when (position) {
            0 -> editFragment
            1 -> ResizeFragment()
            2 -> MoveFragment.newInstance()
            3 -> ColorPickerFragment()
            else -> TreeFragment()
        }
    }
}