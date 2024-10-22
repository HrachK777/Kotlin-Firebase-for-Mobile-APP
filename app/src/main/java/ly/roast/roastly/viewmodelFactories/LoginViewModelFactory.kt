package ly.roast.roastly.viewmodelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ly.roast.roastly.data.repository.UserRepository
import ly.roast.roastly.viewmodel.LoginViewModel

class LoginViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

