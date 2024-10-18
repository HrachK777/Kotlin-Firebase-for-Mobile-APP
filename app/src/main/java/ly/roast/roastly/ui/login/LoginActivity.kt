package ly.roast.roastly.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ly.roast.roastly.databinding.ActivityLoginBinding
import ly.roast.roastly.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Configura ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Observa mudanças no estado de login
        viewModel.loginState.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                finish()
                // Ir para a próxima Activity
            } else {
                Toast.makeText(this, "Erro no login", Toast.LENGTH_SHORT).show()
            }
        })

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.login(email, password)
        }

        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}