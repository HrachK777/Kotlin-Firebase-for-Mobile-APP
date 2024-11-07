package ly.roast.roastly

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    private lateinit var campoEmailUsuario: EditText
    private lateinit var campoCargoUsuario: EditText
    private lateinit var botaoVoltar: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private var imagemUri: Uri? = null
    private var changesMade = false // Track if any changes are made

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        fotoPerfil = findViewById(R.id.user_photo)
        iconeCamera = findViewById(R.id.icon_camera)
        botaoAplicarAlteracoes = findViewById(R.id.saveEditProfileButton)
        campoEmailUsuario = findViewById(R.id.emailInput)
        campoCargoUsuario = findViewById(R.id.jobInput)
        botaoVoltar = findViewById(R.id.icon_back)

        loadUserData()

        iconeCamera.setOnClickListener {
            openImageChooser()
        }

        botaoAplicarAlteracoes.setOnClickListener {
            val newEmail = campoEmailUsuario.text.toString().trim()
            val newJob = campoCargoUsuario.text.toString().trim()

            // Update only if fields are filled or image is selected
            if (newEmail.isNotEmpty()) {
                updateUserEmail(newEmail)
            }
            if (newJob.isNotEmpty()) {
                updateUserJob(newJob)
            }
            if (imagemUri != null) {
                uploadImageToFirebase()
            }
            if (!changesMade) {
                Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show()
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
                        campoEmailUsuario.hint = document.getString("email") ?: ""
                        campoCargoUsuario.hint = document.getString("job") ?: ""
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imagemUri = data.data
            if (imagemUri != null) {
                Picasso.get().load(imagemUri).into(fotoPerfil)
            }
        }
    }

    private fun uploadImageToFirebase() {
        imagemUri?.let { uri ->
            val usuarioEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$usuarioEmail")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val userRef = FirebaseFirestore.getInstance().collection("users").document(usuarioEmail)

                        userRef.set(mapOf("profileImageUrl" to downloadUrl.toString()), SetOptions.merge())
                            .addOnSuccessListener {
                                changesMade = true
                                Toast.makeText(this, "Profile image updated successfully!", Toast.LENGTH_SHORT).show()
                                finishIfChanges()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to update profile image.", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserEmail(newEmail: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val oldEmail = currentUser?.email

        if (oldEmail != null) {
            val passwordInput = EditText(this)
            passwordInput.hint = "Digite sua senha"

            AlertDialog.Builder(this)
                .setTitle("Reautenticação Necessária")
                .setMessage("Por favor, insira sua senha para continuar.")
                .setView(passwordInput)
                .setPositiveButton("Confirmar") { _, _ ->
                    val password = passwordInput.text.toString()

                    if (password.isNotEmpty()) {
                        val credential = EmailAuthProvider.getCredential(oldEmail, password)

                        currentUser.reauthenticate(credential)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    sendVerificationEmail(newEmail)
                                } else {
                                    Toast.makeText(this, "Reauthentication failed. Check your credentials.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun sendVerificationEmail(newEmail: String) {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent to $newEmail. Please check your inbox.", Toast.LENGTH_LONG).show()
                    changesMade = true
                    finishIfChanges()
                } else {
                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUserJob(newJob: String) {
        val usuarioEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val userRef = FirebaseFirestore.getInstance().collection("users").document(usuarioEmail)

        userRef.set(mapOf("job" to newJob), SetOptions.merge())
            .addOnSuccessListener {
                changesMade = true
                Toast.makeText(this, "Job updated successfully!", Toast.LENGTH_SHORT).show()
                finishIfChanges()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update job.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun finishIfChanges() {
        if (changesMade) {
            finish()
        }
    }
}
