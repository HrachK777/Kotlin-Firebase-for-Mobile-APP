package ly.roast.roastly.ui.common

import AddFragment
import FeedbackHistoryFragment
import LeaderboardsFragment
import ReviewFeedFragment
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.R
import ly.roast.roastly.ui.login.LoginActivity
import ly.roast.roastly.ui.profile.ProfileFragment
import ly.roast.roastly.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            loadFragment(AddFragment())
        }

        findViewById<View>(R.id.icon_home).setOnClickListener { loadFragment(ReviewFeedFragment()) }
        findViewById<View>(R.id.icon_ranking).setOnClickListener { loadFragment(LeaderboardsFragment()) }
        findViewById<View>(R.id.icon_add).setOnClickListener { loadFragment(AddFragment()) }
        findViewById<View>(R.id.icon_profile).setOnClickListener { loadFragment(ProfileFragment()) }
        findViewById<View>(R.id.icon_feed).setOnClickListener { loadFragment(FeedbackHistoryFragment()) }

        findViewById<View>(R.id.icon_menu_user).setOnClickListener {
            showMenuPopup(it)
        }

        homeViewModel.deletionState.observe(this) { result ->
            result.onSuccess {
                clearSharedPreferences()
                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Failed to delete account: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showMenuPopup(anchor: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.fragment_menu, null)
        val popupWindow = PopupWindow(
            popupView,
            (230 * resources.displayMetrics.density).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupView.findViewById<TextView>(R.id.item_edit_profile).setOnClickListener {
            loadFragment(ProfileFragment())
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            clearSharedPreferences()
            navigateToLogin()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_delete_account).setOnClickListener {
            showDeleteAccountPrompt()
            popupWindow.dismiss()
        }

        PopupWindowCompat.showAsDropDown(popupWindow, anchor, 0, 10, Gravity.END)
    }

    private fun showDeleteAccountPrompt() {
        val passwordInput = EditText(this).apply {
            hint = "Enter your password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("Temos pena que vais deixar o Roast.ly")
            .setMessage("Insere a password para confirmar a operação")
            .setView(passwordInput)
            .setPositiveButton("Confirmar") { dialog, _ ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    homeViewModel.deleteAccount(password)
                } else {
                    Toast.makeText(this, "Tens que inserir a password.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            // Esta linha e para fazer clear a stack de atividades
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun clearSharedPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
