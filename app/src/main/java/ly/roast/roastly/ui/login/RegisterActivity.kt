package ly.roast.roastly.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.data.model.User
import ly.roast.roastly.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    // ViewModel injetado usando o ViewModelProvider
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val surname = binding.surnameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confirmPassword = binding.passwordConfirmationInput.text.toString().trim()
            val job = binding.jobInput.text.toString().trim()

            // Verificar se as passwords coincidem
            if (password != confirmPassword) {
                Toast.makeText(this, "As palavra-passes não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validação do email
            if (!isValidEmail(email)) {
                Toast.makeText(this, "O email deve terminar com 'msft.cesae.pt' ou 'cesae.pt'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar utilizador no Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: return@addOnSuccessListener

                    // Criar objeto User para guardar na Firestore
                    val user = User(
                        name = name,
                        surname = surname,
                        email = email,
                        job = job
                    )

                    // Guardar os dados do utilizador no Firestore
                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registo bem-sucedido!", Toast.LENGTH_SHORT).show()
                            finish()
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao guardar dados!", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao registar utilizador!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Função para verificar se o email termina com 'msft.cesae.pt' ou 'cesae.pt' e contem um arroba com texto antes
    private fun isValidEmail(email: String): Boolean {
        val arrobaIndex = email.indexOf('@')
        return arrobaIndex > 0 && (email.endsWith("msft.cesae.pt") || email.endsWith("cesae.pt"))    }
}