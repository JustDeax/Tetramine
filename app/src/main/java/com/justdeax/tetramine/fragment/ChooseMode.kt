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
import com.justdeax.tetramine.util.constant.GameType

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
            classic.setOnClickListener {
                startGame(GameType.CLASSIC)
            }
            practice.setOnClickListener {
                startGame(GameType.PRACTICE)
            }
            sprint.setOnClickListener {
                //startGame(GameType.SPRINT)
                notAvailable(requireContext(), getString(R.string.sprint_mode))
            }
            modern.setOnClickListener {
                //startGame(GameType.MODERN)
                notAvailable(requireContext(), getString(R.string.modern_mode))
            }
        }
    }

    private fun startGame(type: String) {
        val game = Intent(requireActivity(), GameActivity::class.java)
        game.putExtra(GameType.TYPE, type)
        startActivity(game)
    }

    private fun notAvailable(context: Context, mode: String) {
        Toast.makeText(
            context,
            "$mode not available",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}