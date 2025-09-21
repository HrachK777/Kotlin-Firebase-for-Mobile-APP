package ly.roast.roastly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private lateinit var fotoPerfil: CircleImageView
    private lateinit var iconeCamera: ImageView
    private lateinit var botaoAplicarAlteracoes: Button
    private lateinit var campoCargoUsuario: EditText
    private lateinit var botaoVoltar: ImageView
    private lateinit var campoSenhaAtual: EditText
    private lateinit var campoNovaSenha: EditText
    private lateinit var campoConfirmarNovaSenha: EditText

    private val PICK_IMAGE_REQUEST = 1
    private var imagemUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        fotoPerfil = findViewById(R.id.user_photo)
        iconeCamera = findViewById(R.id.icon_camera)
        botaoAplicarAlteracoes = findViewById(R.id.saveEditProfileButton)
        campoCargoUsuario = findViewById(R.id.jobInput)
        botaoVoltar = findViewById(R.id.icon_back)
        campoSenhaAtual = findViewById(R.id.passwordInput)
        campoNovaSenha = findViewById(R.id.newPasswordInput)
        campoConfirmarNovaSenha = findViewById(R.id.newPasswordConfirmationInput)

        loadUserData()

        iconeCamera.setOnClickListener {
            openImageChooser()
        }

        botaoAplicarAlteracoes.setOnClickListener {
            val newJob = campoCargoUsuario.text.toString().trim()
            val currentPassword = campoSenhaAtual.text.toString().trim()
            val newPassword = campoNovaSenha.text.toString().trim()
            val confirmPassword = campoConfirmarNovaSenha.text.toString().trim()

            if (newJob.isNotEmpty()) {
                updateUserJob(newJob)
            }

            if (imagemUri != null) {
                uploadImageToFirebase()
            }

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (newPassword == confirmPassword) {
                    updatePassword(currentPassword, newPassword)
                } else {
                    Toast.makeText(this, "As senhas novas não coincidem.", Toast.LENGTH_SHORT).show()
                }
            } else if (newJob.isEmpty() && imagemUri == null) {
                Toast.makeText(this, "Nenhuma alteração a ser salva", Toast.LENGTH_SHORT).show()
            }
        }

        botaoVoltar.setOnClickListener {
            finish()
        }
    }

    private fun loadUserData() {
        val usuarioEmail = FirebaseAuth.getInstance().currentUser?.email
        if (usuarioEmail != null) {
            FirebaseFirestore.getInstance().collection("users").document(usuarioEmail)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val job = document.getString("job")
                        if (!job.isNullOrEmpty()) {
                            campoCargoUsuario.setText(job)
                        }

                        val profileImageUrl = document.getString("profileImageUrl")
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Picasso.get().load(profileImageUrl).into(fotoPerfil)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar dados do usuário.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecionar imagem"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagemUri = data.data
            if (imagemUri != null) {
                fotoPerfil.setImageURI(imagemUri)
            }
        }
    }

    private fun uploadImageToFirebase() {
        val usuarioEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$usuarioEmail")

        imagemUri?.let { uri ->
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val userRef = FirebaseFirestore.getInstance().collection("users").document(usuarioEmail)

                        val updates = mapOf("profileImageUrl" to downloadUrl.toString())
                        userRef.set(updates, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Imagem de perfil atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Falha ao atualizar a imagem de perfil.", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falha ao enviar a imagem.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserJob(newJob: String) {
        val usuarioEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val userRef = FirebaseFirestore.getInstance().collection("users").document(usuarioEmail)

        val updates = mapOf("job" to newJob)
        userRef.set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Cargo atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao atualizar o cargo.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePassword(currentPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val usuarioEmail = user?.email

        if (user != null && usuarioEmail != null) {
            val credential = EmailAuthProvider.getCredential(usuarioEmail, currentPassword)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("EditProfileActivity", e.toString())
                            Toast.makeText(this, "Falha ao atualizar a senha.", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falha na reautenticação. Verifique sua senha atual.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
