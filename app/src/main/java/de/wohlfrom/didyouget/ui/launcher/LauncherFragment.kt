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
    override fun onResume() {
        super.onResume()
        findNavController(this).navigate(R.id.showLogin)
    }
}
