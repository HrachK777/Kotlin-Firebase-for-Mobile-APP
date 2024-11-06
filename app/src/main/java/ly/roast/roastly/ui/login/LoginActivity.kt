package ly.roast.roastly.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ly.roast.roastly.ForgotPasswordActivity
import ly.roast.roastly.data.repository.UserRepository
import ly.roast.roastly.databinding.ActivityLoginBinding
import ly.roast.roastly.ui.common.HomeActivity
import ly.roast.roastly.viewmodel.LoginViewModel
import ly.roast.roastly.viewmodelFactories.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Configura ViewModel pela factory porque temos que adicionar dependencia ao userRepository
        val userRepository = UserRepository(this)
        val viewModelFactory = LoginViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        // Observa mudanças no estado de login
        viewModel.loginState.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "Erro ao efetuar login", Toast.LENGTH_SHORT).show()
            }
        })

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            viewModel.login(email, password)
        }

        // Ir para a activity de criar conta
        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Ir para a activity de resetar password
        binding.passwordRemember.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}