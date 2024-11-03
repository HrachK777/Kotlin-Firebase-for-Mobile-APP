package ly.roast.roastly.ui.common

import AddFragment
import ReviewFeedFragment
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.PopupWindowCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import ly.roast.roastly.R
import ly.roast.roastly.ui.login.LoginActivity
import ly.roast.roastly.ui.profile.ProfileFragment
import android.content.Context

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            loadFragment(AddFragment())
        }

        findViewById<View>(R.id.icon_home).setOnClickListener { loadFragment(ReviewFeedFragment()) }
        //findViewById<View>(R.id.icon_feed).setOnClickListener { loadFragment(HistoricFragment()) }
        findViewById<View>(R.id.icon_add).setOnClickListener { loadFragment(AddFragment()) }
        //findViewById<View>(R.id.icon_ranking).setOnClickListener { loadFragment(RankingFragment()) }
        findViewById<View>(R.id.icon_profile).setOnClickListener { loadFragment(ProfileFragment()) }

        findViewById<View>(R.id.icon_menu_user).setOnClickListener {
            showMenuPopup(it)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    private fun showMenuPopup(anchor: View) {
        // Inflate the menu layout
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

            val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            // Esta linha em baixo e para fazer clear a stack de atividades que possam ter anteriormente
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.item_delete_account).setOnClickListener {
            popupWindow.dismiss()
        }

        PopupWindowCompat.showAsDropDown(popupWindow, anchor, 0, 10, Gravity.END)
    }

}
