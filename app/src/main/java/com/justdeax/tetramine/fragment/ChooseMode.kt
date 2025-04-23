package com.justdeax.tetramine.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.justdeax.tetramine.R
import com.justdeax.tetramine.activity.GameActivity
import com.justdeax.tetramine.databinding.FragmentChooseModeBinding
import com.justdeax.tetramine.util.notAvailable

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

        val game = Intent(activity, GameActivity::class.java)

        binding.apply {
            classic.setOnClickListener {
                startActivity(game)
            }
            practice.setOnClickListener {
                notAvailable(requireContext(), getString(R.string.practice_mode))
            }
            sprint.setOnClickListener {
                notAvailable(requireContext(), getString(R.string.sprint_mode))
            }
            modern.setOnClickListener {
                notAvailable(requireContext(), getString(R.string.modern_mode))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}