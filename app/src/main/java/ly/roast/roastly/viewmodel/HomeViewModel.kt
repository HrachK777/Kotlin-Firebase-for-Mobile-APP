package ly.roast.roastly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _deletionState = MutableLiveData<Result<Boolean>>()
    private val firestore = FirebaseFirestore.getInstance()
    val deletionState: LiveData<Result<Boolean>> get() = _deletionState

    fun deleteAccount(password: String) {
        val user = auth.currentUser ?: return
        val userEmail = user.email ?: return

        val credential = EmailAuthProvider.getCredential(userEmail, password)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                firestore.collection("users").document(userEmail)
                    .delete()
                    .addOnSuccessListener {
                        user.delete()
                            .addOnSuccessListener {
                                _deletionState.value = Result.success(true)
                            }
                            .addOnFailureListener { exception ->
                                _deletionState.value = Result.failure(exception)
                            }
                    }
                    .addOnFailureListener { exception ->
                        _deletionState.value = Result.failure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                _deletionState.value = Result.failure(exception)
            }
    }
}