package ly.roast.roastly.ui.common

import AddFragment
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ly.roast.roastly.R
import ly.roast.roastly.ui.profile.EditProfileActivity
import ly.roast.roastly.ui.profile.ProfileFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)  // Using your provided layout file

        // Set the default fragment when the activity starts
        if (savedInstanceState == null) {
            loadFragment(AddFragment())  // Set the default fragment
        }

        // Add click listeners for the icons
        //findViewById<View>(R.id.icon_home).setOnClickListener { loadFragment(HomeFragment()) }
        //findViewById<View>(R.id.icon_feed).setOnClickListener { loadFragment(HistoricFragment()) }
        findViewById<View>(R.id.icon_add).setOnClickListener { loadFragment(AddFragment()) }
        //findViewById<View>(R.id.icon_ranking).setOnClickListener { loadFragment(RankingFragment()) }
        findViewById<View>(R.id.icon_profile).setOnClickListener { loadFragment(ProfileFragment()) }
    }

    // Function to load the selected fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)  // fragment_container is your FrameLayout ID
            .commit()
    }
}
