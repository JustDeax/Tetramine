package com.justdeax.tetramine.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.justdeax.tetramine.util.constant.Delay
import com.justdeax.tetramine.util.showResetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Statistics : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            getStatistics(total = true)

            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                when (checkedIds.firstOrNull()) {
                    R.id.total -> getStatistics(total = true)
                    R.id.best -> getStatistics(total = false)
                }
            }
            reset.setOnClickListener {
                requireActivity().apply {
                    showResetDialog(R.string.reset_stats) {
                        resetStats()
                        when (binding.chipGroup.checkedChipId) {
                            R.id.total -> getStatistics(total = true)
                            R.id.best -> getStatistics(total = false)
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                delay(Delay.MEDIUM * 2)
                reset.visibility = View.VISIBLE
            }
        }
    }

    private fun getStatistics(total: Boolean) {
        val (score, lines, pieces, fourLines, tSpins) = with(requireActivity()) {
            if (total)
                listOf(totalScore, totalLines, totalPieces, total4Lines, totalTSpins)
            else
                listOf(bestScore, bestLines, bestPieces, best4Lines, bestTSpins)
        }

        binding.apply {
            animateInteger(scoreNumber, score, Delay.ANIMATION_SCORE)
            animateInteger(linesNumber, lines, Delay.ANIMATION_LINES)
            animateInteger(piecesNumber, pieces, Delay.ANIMATION_PIECES)
            animateInteger(fourLinesNumber, fourLines, Delay.ANIMATION_FOUR_LINES)
            animateInteger(tSpinsNumber, tSpins, Delay.ANIMATION_T_SPINS)
        }
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