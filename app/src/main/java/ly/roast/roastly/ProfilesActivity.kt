import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import ly.roast.roastly.ProfileUserFragment
import ly.roast.roastly.R
import ly.roast.roastly.ui.profile.ProfileFragment

class ProfilesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)

    }
/*
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

    }

 */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

}


