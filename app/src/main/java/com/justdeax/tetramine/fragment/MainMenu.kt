package com.justdeax.tetramine.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.justdeax.tetramine.PreferenceManager.gameSnapshotJson
import com.justdeax.tetramine.PreferenceManager.versionName
import com.justdeax.tetramine.R
import com.justdeax.tetramine.activity.GameActivity
import com.justdeax.tetramine.databinding.FragmentMainMenuBinding
import com.justdeax.tetramine.util.constant.GameMode

class MainMenu : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val versionText = getString(R.string.version, requireActivity().versionName)

        binding.apply {
            version.text = versionText
            resumeGame.isEnabled = requireActivity().gameSnapshotJson != ""

            chooseMode.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenu_to_chooseGame)
            }
            binding.resumeGame.setOnClickListener {
                val intent = Intent(requireActivity(), GameActivity::class.java).apply {
                    putExtra(GameMode.MODE, GameMode.RESTORE)
                }
                startActivity(intent)
            }
            binding.resumeGame.setOnLongClickListener {
                requireActivity().gameSnapshotJson = ""
                resumeGame.isEnabled = false
                true
            }
            statistics.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenu_to_statistics)
            }
            settings.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenu_to_settings)
            }
            about.setOnClickListener {
                findNavController().navigate(R.id.action_mainMenu_to_aboutGame)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}