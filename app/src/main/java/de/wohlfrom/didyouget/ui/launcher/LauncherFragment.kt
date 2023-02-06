package de.wohlfrom.didyouget.ui.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import de.wohlfrom.didyouget.R
import de.wohlfrom.didyouget.databinding.FragmentLauncherBinding

class LauncherFragment : Fragment() {

    private lateinit var viewModel: LauncherViewModel
    private lateinit var binding: FragmentLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LauncherViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLauncherBinding.inflate(layoutInflater)
        binding.launcherIcon.setOnClickListener {
            findNavController(this).navigate(R.id.showLogin)
        }
        return binding.root
    }
}
