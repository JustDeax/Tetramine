package com.justdeax.tetramine.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.justdeax.tetramine.R
import com.justdeax.tetramine.activity.GameActivity
import com.justdeax.tetramine.databinding.FragmentChooseModeBinding
import com.justdeax.tetramine.util.constant.GameMode

class ChooseMode : Fragment() {
    private var _binding: FragmentChooseModeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            classic.setOnClickAndLongListener {
                startGame(GameMode.CLASSIC)
            }
            practice.setOnClickAndLongListener {
                startGame(GameMode.PRACTICE)
            }
            sprint.setOnClickAndLongListener {
                notAvailable(requireContext(), getString(R.string.sprint_mode))
            }
            modern.setOnClickAndLongListener {
                notAvailable(requireContext(), getString(R.string.modern_mode))
            }
        }
    }

    private fun View.setOnClickAndLongListener(action: () -> Boolean) {
        setOnClickListener { action() }
        setOnLongClickListener { action() }
    }

    private fun startGame(mode: String): Boolean {
        val intent = Intent(requireActivity(), GameActivity::class.java).apply {
            putExtra(GameMode.MODE, mode)
        }
        startActivity(intent)
        return true
    }

    private fun notAvailable(context: Context, mode: String): Boolean {
        Toast.makeText(
            context,
            "$mode not available",
            Toast.LENGTH_SHORT
        ).show()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}