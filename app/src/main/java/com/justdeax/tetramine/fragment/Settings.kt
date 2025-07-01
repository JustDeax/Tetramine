package com.justdeax.tetramine.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.justdeax.tetramine.databinding.FragmentSettingsBinding

class Settings : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val views = getAllViewsWithId(binding.root)
            getSettings(views)
        }
    }

    private fun getAllViewsWithId(view: View): List<View> {
        val result = mutableListOf<View>()
        if (view.id != View.NO_ID)
            result += view
        if (view is ViewGroup)
            for (i in 0 until view.childCount)
                result += getAllViewsWithId(view.getChildAt(i))
        return result
    }


    private fun getSettings(views: List<View>) {
        for (v in views) {
            val idName = try {
                v.resources.getResourceEntryName(v.id)
            } catch (_: Resources.NotFoundException) {}

            when (v) {
                is MaterialSwitch -> {

                }
                is Slider -> {

                }
                is MaterialButton -> {

                }
                is ChipGroup -> {

                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}