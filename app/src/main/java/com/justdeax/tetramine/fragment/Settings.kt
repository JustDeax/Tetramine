package com.justdeax.tetramine.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.justdeax.tetramine.PreferenceManager.cellCornerRadius
import com.justdeax.tetramine.PreferenceManager.cellSpacing
import com.justdeax.tetramine.PreferenceManager.emptyCellOpacity
import com.justdeax.tetramine.PreferenceManager.is2DirectionRotation
import com.justdeax.tetramine.PreferenceManager.isDynamicColors
import com.justdeax.tetramine.PreferenceManager.isMusicEnable
import com.justdeax.tetramine.PreferenceManager.isNightThemeMode
import com.justdeax.tetramine.PreferenceManager.isShowGhostPiece
import com.justdeax.tetramine.PreferenceManager.maxTimeHDT
import com.justdeax.tetramine.PreferenceManager.minSoftDropsHDT
import com.justdeax.tetramine.PreferenceManager.resetSettings
import com.justdeax.tetramine.PreferenceManager.xSensitivity
import com.justdeax.tetramine.PreferenceManager.ySensitivity
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.FragmentSettingsBinding
import com.justdeax.tetramine.util.showNumberInputDialog
import com.justdeax.tetramine.util.showResetDialog
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            getSettings()
        }
        setSettings()

        binding.apply {
            reset.setOnClickListener {
                with(requireActivity()) {
                    showResetDialog(R.string.reset_settings) {
                        resetSettings()
                        getSettings()
                    }
                }
            }
        }
    }

    private fun setSettings() {
        with(requireActivity()) {
            binding.theme.setOnCheckedStateChangeListener { _, checkedIds ->
                isNightThemeMode = checkedIds.firstOrNull() == R.id.dark
                AppCompatDelegate.setDefaultNightMode(
                    if (isNightThemeMode)
                        AppCompatDelegate.MODE_NIGHT_YES
                    else
                        AppCompatDelegate.MODE_NIGHT_NO
                )
            }
            binding.dynamicColors.setOnCheckedChangeListener { _, isChecked ->
                isDynamicColors = isChecked
                activity?.recreate()
            }
            binding.showGhostPiece.setOnCheckedChangeListener { _, isChecked ->
                isShowGhostPiece = isChecked
            }
            binding.emptyCellOpacity.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) emptyCellOpacity = value
            }
            binding.cellCornerRadius.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) cellCornerRadius = value
            }
            binding.cellSpacing.addOnChangeListener { slider, value, fromUser ->
                if (fromUser) cellSpacing = value
            }
            binding.music.setOnCheckedChangeListener { _, isChecked ->
                isMusicEnable = isChecked
            }
            binding.x.setOnChangeListener(0.1, 2.0, true, R.string.x) { result ->
                xSensitivity = result.toFloat()
            }
            binding.y.setOnChangeListener(0.1, 2.0, true, R.string.y) { result ->
                ySensitivity = result.toFloat()
            }
            binding.maxTime.setOnChangeListener(50, 500, false, R.string.max_time) { result ->
                maxTimeHDT = result.toInt()
            }
            binding.minSoftDrops.setOnChangeListener(1, 7, false, R.string.min_soft_drops) { result ->
                minSoftDropsHDT = result.toInt()
            }
            binding.biDirectionRotation.setOnCheckedChangeListener { _, isChecked ->
                is2DirectionRotation = isChecked
            }
        }
    }

    private fun getSettings() {
        with(requireActivity()) {
            binding.theme.check(if (isNightThemeMode) R.id.dark else R.id.light)
            binding.dynamicColors.isChecked = isDynamicColors
            binding.showGhostPiece.isChecked = isShowGhostPiece
            binding.emptyCellOpacity.value = emptyCellOpacity
            binding.cellCornerRadius.value = cellCornerRadius
            binding.cellSpacing.value = cellSpacing
            binding.music.isChecked = isMusicEnable
            binding.x.text = formatString(R.string.x, xSensitivity)
            binding.y.text = formatString(R.string.y, ySensitivity)
            binding.maxTime.text = formatString(R.string.max_time, maxTimeHDT)
            binding.minSoftDrops.text = formatString(R.string.min_soft_drops, minSoftDropsHDT)
            binding.biDirectionRotation.isChecked = is2DirectionRotation
        }
    }

    private fun formatString(textId: Int, number: Number): String {
        return getString(textId, number.toString())
    }

    private fun Button.setOnChangeListener(min: Number, max: Number, isFloat: Boolean, textId: Int, onResult: (Number) -> Unit) {
        setOnClickListener {
            requireActivity().showNumberInputDialog(
                min,
                max,
                isFloat,
                R.string.enter_a_value
            ) { result ->
                onResult(result)
                text = getString(textId, result.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}