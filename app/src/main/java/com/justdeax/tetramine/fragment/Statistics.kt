package com.justdeax.tetramine.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.PreferenceManager.best4Lines
import com.justdeax.tetramine.PreferenceManager.bestLines
import com.justdeax.tetramine.PreferenceManager.bestPieces
import com.justdeax.tetramine.PreferenceManager.bestScore
import com.justdeax.tetramine.PreferenceManager.bestTSpins
import com.justdeax.tetramine.PreferenceManager.resetStats
import com.justdeax.tetramine.PreferenceManager.total4Lines
import com.justdeax.tetramine.PreferenceManager.totalLines
import com.justdeax.tetramine.PreferenceManager.totalPieces
import com.justdeax.tetramine.PreferenceManager.totalScore
import com.justdeax.tetramine.PreferenceManager.totalTSpins
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Statistics : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var showResetButtonJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            showStatistics(total = true)

            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                when (checkedIds.firstOrNull()) {
                    R.id.total -> showStatistics(total = true)
                    R.id.best -> showStatistics(total = false)
                }
            }
            reset.setOnClickListener { snowResetStatisticsDialog() }
        }
    }

    private fun showStatistics(total: Boolean) = with(requireActivity()) {
        binding.apply {
            animateInteger(scoreNumber, if (total) totalScore else bestScore, 3400L)
            animateInteger(linesNumber, if (total) totalLines else bestLines, 1200L)
            animateInteger(piecesNumber, if (total) totalPieces else bestPieces, 2000L)
            animateInteger(fourLinesNumber, if (total) total4Lines else best4Lines, 800L)
            animateInteger(tSpinsNumber, if (total) totalTSpins else bestTSpins, 1000L)
        }
        showResetButtonJob?.cancel()
        showResetButtonJob = viewLifecycleOwner.lifecycleScope.launch {
            binding.reset.visibility = View.GONE
            delay(3400L)
            binding.reset.visibility = View.VISIBLE
        }
    }

    private fun snowResetStatisticsDialog() {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.reset)
            .setMessage(R.string.reset_desc)
            .setPositiveButton(R.string.confirm) { _, _ ->
                requireActivity().resetStats()
                when (binding.chipGroup.checkedChipId) {
                    R.id.total -> showStatistics(total = true)
                    R.id.best -> showStatistics(total = false)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
        dialog.show()
    }

    private fun animateInteger(view: TextView, newInt: Int, duration: Long) {
        val animator = ValueAnimator.ofInt(0, newInt)
        animator.addUpdateListener { valueAnimator ->
            val updatedInt = valueAnimator.animatedValue as Int
            view.text = updatedInt.toString()
        }
        animator.duration = duration
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}