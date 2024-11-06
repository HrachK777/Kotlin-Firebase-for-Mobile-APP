package ly.roast.roastly.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import ly.roast.roastly.data.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // Usa LiveData para observar o estado do login
    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    fun login(email: String, password: String) {
        userRepository.loginWithEmailPass(email, password) {  user, error ->
            if(user != null) {
                userRepository.saveUserToSharedPreferences(user.uid)
                //Log.d("LoginViewModel", "SUCESSSOOOOO")
                _loginState.postValue(true)
            } else {
                //Log.d("LoginViewModel", "errooooo")
                _loginState.postValue(false)
            }
        }
    }

    fun checkIfUserIsLoggedIn(): Boolean {
        return userRepository.isUserLoggedIn()
    }

}