package ly.roast.roastly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // LiveData para observar o estado de registo
    private val _registerState = MutableLiveData<Boolean>()
    val registerState: LiveData<Boolean> get() = _registerState

    // Função para registar um novo utilizador
    fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _registerState.value = true // Sucesso
            }
            .addOnFailureListener {
                _registerState.value = false // Falha
            }
    }
}