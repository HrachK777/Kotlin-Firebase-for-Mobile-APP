package ly.roast.roastly.viewmodel

import User
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableLiveData<User>()
    val userProfile: LiveData<User> get() = _userProfile

    fun loadUserProfile(userId: String) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                _userProfile.value = document.toObject(User::class.java)
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileViewModel", "Error fetching user profile", exception)
            }
    }
}