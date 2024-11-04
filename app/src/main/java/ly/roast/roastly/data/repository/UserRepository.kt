package ly.roast.roastly.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserRepository(private val context: Context) {
    fun loginWithEmailPass(
        email: String,
        password: String,
        callback: (FirebaseUser?, Exception?) -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    callback(user, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }

    fun saveUserToSharedPreferences(userId: String) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun logout() {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        FirebaseAuth.getInstance().signOut()
    }
}