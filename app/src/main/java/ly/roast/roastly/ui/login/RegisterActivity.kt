package ly.roast.roastly.ui.login

import User
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ly.roast.roastly.R
import ly.roast.roastly.databinding.ActivityRegisterBinding
import ly.roast.roastly.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val viewModel: RegisterViewModel by viewModels() // Using ViewModelProvider for ViewModel injection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeViewModel()

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val surname = binding.surnameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confirmPassword = binding.passwordConfirmationInput.text.toString().trim()
            val job = binding.jobInput.text.toString().trim()

            if (password != confirmPassword) {
                Toast.makeText(this, "A password não está correta!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "O email deve terminar com 'msft.cesae.pt' ou 'cesae.pt'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.registerUser(email, password, name, surname, job)
        }

        // Encontre o ImageView do ícone de voltar
        val backButton: ImageView = findViewById(R.id.icon_back)

        // Configurar o listener de clique
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                Toast.makeText(this, "Não foi possivel criar a conta!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val arrobaIndex = email.indexOf('@')
        return arrobaIndex > 0 && (email.endsWith("msft.cesae.pt") || email.endsWith("cesae.pt"))
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
