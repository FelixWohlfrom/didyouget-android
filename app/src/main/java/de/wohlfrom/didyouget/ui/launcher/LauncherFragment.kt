package de.wohlfrom.didyouget.ui.launcher

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import de.wohlfrom.didyouget.R

class LauncherFragment : Fragment() {
    override fun onResume() {
        super.onResume()
        findNavController(this).navigate(R.id.showLogin)
    }
}
