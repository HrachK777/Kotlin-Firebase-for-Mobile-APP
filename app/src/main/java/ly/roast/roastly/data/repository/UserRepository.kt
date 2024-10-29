package ly.roast.roastly.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserRepository(private val context: Context) {
    fun loginWithEmailPass(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit) {
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
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val user = FirebaseAuth.getInstance().currentUser
        return isLoggedIn && user != null
    }

    fun logout() {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        FirebaseAuth.getInstance().signOut()
    }
}