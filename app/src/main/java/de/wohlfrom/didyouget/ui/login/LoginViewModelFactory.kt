package de.wohlfrom.didyouget.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.wohlfrom.didyouget.data.LoginRepository
import de.wohlfrom.didyouget.data.sources.LoginDataSource

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory constructor(private val activity: Activity) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(),
                    activity = this.activity
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
