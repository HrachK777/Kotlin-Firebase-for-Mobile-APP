package ly.roast.roastly.viewmodel

import User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _registerState = MutableLiveData<Boolean>()
    val registerState: LiveData<Boolean> get() = _registerState

    fun registerUser(email: String, password: String, name: String, surname: String, job: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = User(name = name, surname = surname, email = email, job = job)

                db.collection("users").document(email).set(user)
                    .addOnSuccessListener {
                        _registerState.value = true
                    }
                    .addOnFailureListener {
                        _registerState.value = false
                    }
            }
            .addOnFailureListener {
                _registerState.value = false
            }
    }
}
