package ly.roast.roastly.ui.common

import AddFragment
import FeedbackHistoryFragment
import LeaderboardsFragment
import ReviewFeedFragment
import User
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.media.Image
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ly.roast.roastly.EditProfileActivity
import ly.roast.roastly.R
import ly.roast.roastly.data.repository.UserRepository
import ly.roast.roastly.ui.login.LoginActivity
import ly.roast.roastly.ui.profile.ProfileFragment
import ly.roast.roastly.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val userRepository by lazy { UserRepository(applicationContext) }
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        checkForNewMonthAndResetRatings()
        testDisplayMonthlyTopUserWithoutReset()

        if (savedInstanceState == null) {
            loadFragment(AddFragment())
        }

        val iconUnderBar = findViewById<ImageView>(R.id.icon_under_bar)

        findViewById<View>(R.id.icon_home).setOnClickListener {
            loadFragment(ReviewFeedFragment())
            moveUnderBar(iconUnderBar, it)
            iconUnderBar.visibility = View.VISIBLE
        }
        findViewById<View>(R.id.icon_ranking).setOnClickListener {
            loadFragment(LeaderboardsFragment())
            moveUnderBar(iconUnderBar, it)
            iconUnderBar.visibility = View.VISIBLE
        }
        findViewById<View>(R.id.icon_add).setOnClickListener {
            loadFragment(AddFragment())
            iconUnderBar.visibility = View.INVISIBLE
        }
        findViewById<View>(R.id.icon_profile).setOnClickListener {
            loadFragment(ProfileFragment())
            moveUnderBar(iconUnderBar, it)
            iconUnderBar.visibility = View.VISIBLE
        }
        findViewById<View>(R.id.icon_feed).setOnClickListener {
            loadFragment(FeedbackHistoryFragment())
            moveUnderBar(iconUnderBar, it)
            iconUnderBar.visibility = View.VISIBLE
        }


        findViewById<View>(R.id.icon_menu_user).setOnClickListener {
            showMenuPopup(it)
        }

        homeViewModel.deletionState.observe(this) { result ->
            result.onSuccess {
                clearSharedPreferences()
                Toast.makeText(this, "Conta eliminada com sucesso!", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Erro ao eliminar a conta: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun checkForNewMonthAndResetRatings() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val lastCheckedMonth = sharedPreferences.getInt("lastCheckedMonth", -1)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

        if (lastCheckedMonth == -1) {
            sharedPreferences.edit().putInt("lastCheckedMonth", currentMonth).apply()
            return
        }

        if (currentMonth != lastCheckedMonth) {
            findAndResetTopMonthlyUser()
            sharedPreferences.edit().putInt("lastCheckedMonth", currentMonth).apply()
        }
    }


    private fun findAndResetTopMonthlyUser() {
        firestore.collection("users")
            .orderBy("averageMonthRating", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val topUser = result.documents.firstOrNull()
                topUser?.let { document ->
                    val user = document.toObject(User::class.java)
                    val userName = document.getString("name")
                    val userEmail = document.getString("email")
                    val currentWins = document.getLong("employeeOfTheMonthWins") ?: 0L

                    if (userEmail != null) {
                        firestore.collection("users").document(userEmail)
                            .update("employeeOfTheMonthWins", currentWins + 1)
                            .addOnSuccessListener {
                                val inflater = LayoutInflater.from(this)
                                val popupView = inflater.inflate(R.layout.popup_congratulations, null)

                                val userNameView = popupView.findViewById<TextView>(R.id.user_name_popup)
                                val userPhotoView = popupView.findViewById<ImageView>(R.id.user_photo_popup)

                                userNameView.text = userName ?: "User"

                                if (user != null) {
                                    loadProfilePhoto(user, userPhotoView)
                                } else {
                                    Log.e("HomeActivity", "Error getting top user.")
                                }

                                val popupWindow = PopupWindow(
                                    popupView,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    true
                                )

                                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)

                                val closeButton = popupView.findViewById<Button>(R.id.close_popup_button)
                                closeButton.setOnClickListener {
                                    popupWindow.dismiss()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("HomeActivity", "Error incrementing wins for top user", e)
                            }
                    } else {
                        Log.e("HomeActivity", "Error getting user email.")
                    }
                }

                resetAllUserMonthlyData()
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Error fetching top monthly user", exception)
                resetAllUserMonthlyData()
            }
    }

    private fun loadProfilePhoto(user: User, imageView: ImageView) {
        if (user.profileImageUrl.isNotEmpty()) {
            Picasso.get().load(user.profileImageUrl).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.profile_default_image)
        }
    }

    fun testDisplayMonthlyTopUserWithoutReset() {
        firestore.collection("users")
            .orderBy("averageMonthRating", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val topUser = result.documents.firstOrNull()?.toObject(User::class.java)
                topUser?.let { user ->
                    val inflater = LayoutInflater.from(this)
                    val popupView = inflater.inflate(R.layout.popup_congratulations, null)

                    val userNameView = popupView.findViewById<TextView>(R.id.user_name_popup)
                    val userPhotoView = popupView.findViewById<ImageView>(R.id.user_photo_popup)

                    userNameView.text = user.name
                    loadProfilePhoto(user, userPhotoView)

                    val popupWindow = PopupWindow(
                        popupView,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        true
                    )

                    popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)

                    val closeButton = popupView.findViewById<Button>(R.id.close_popup_button)
                    closeButton.setOnClickListener {
                        popupWindow.dismiss()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Error fetching top monthly user", exception)
            }
    }

    private fun resetAllUserMonthlyData() {
        firestore.collection("users").get()
            .addOnSuccessListener { documents ->
                val batch = firestore.batch()
                for (document in documents) {
                    val userRef = firestore.collection("users").document(document.id)
                    batch.update(userRef, "averageMonthRating", 0f, "reviewsThisMonth", 0)
                }
                batch.commit().addOnSuccessListener {
                    Log.d("HomeActivity", "Monthly data reset successfully for all users.")
                }.addOnFailureListener { exception ->
                    Log.e("HomeActivity", "Error resetting monthly data", exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeActivity", "Error accessing user data", exception)
            }
    }

    private fun moveUnderBar(iconUnderBar: ImageView, clickedIcon: View) {
        val constraintSet = ConstraintSet()
        val parent = findViewById<ConstraintLayout>(R.id.footer)
        constraintSet.clone(parent)

        constraintSet.connect(iconUnderBar.id, ConstraintSet.START, clickedIcon.id, ConstraintSet.START)
        constraintSet.connect(iconUnderBar.id, ConstraintSet.END, clickedIcon.id, ConstraintSet.END)
        constraintSet.connect(iconUnderBar.id, ConstraintSet.TOP, clickedIcon.id, ConstraintSet.BOTTOM, 5) // Abaixo do ícone com margem
        constraintSet.applyTo(parent)
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
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_logout).setOnClickListener {
            userRepository.logout()
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
            hint = "Introduz a tua password"
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
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
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
