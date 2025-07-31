package com.justdeax.tetramine.fragment

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.justdeax.tetramine.PreferenceManager.versionName
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.FragmentAboutGameBinding

class AboutGame : Fragment() {
    private var _binding: FragmentAboutGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val versionString = getString(R.string.version, requireActivity().versionName) + "\nCheck Updates"
        val authorString = getString(R.string.author, "github.com/JustDeax")
        val projectString = getString(R.string.project, "github.com/JustDeax/Tetramine")

        val versionLink = "https://github.com/JustDeax/Tetramine/releases"
        val authorLink = "https://github.com/JustDeax"
        val projectLink = "https://github.com/JustDeax/Tetramine"

        binding.apply {
            versionText.text = versionString
            authorText.text = authorString
            projectText.text = projectString

            version.setOnClickAndLongListener(versionLink)
            author.setOnClickAndLongListener(authorLink)
            project.setOnClickAndLongListener(projectLink)
        }
    }

    private fun View.setOnClickAndLongListener(text: String) {
        setOnClickListener { openLink(text) }
        setOnLongClickListener { copyText(text); true }
    }

    private fun openLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {}
    }

    private fun copyText(text: String) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}