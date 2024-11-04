package ly.roast.roastly.ui.common

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ly.roast.roastly.R
import ly.roast.roastly.data.repository.UserRepository
import ly.roast.roastly.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val userRepository by lazy { UserRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (userRepository.isUserLoggedIn()) {
                // User is logged in, navigate to HomeActivity
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // User is not logged in, navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
