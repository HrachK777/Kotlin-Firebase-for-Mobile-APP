package ly.roast.roastly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {

    private lateinit var userEmail: TextView
    private lateinit var userJob: TextView
    private lateinit var userPhoto: ImageView
    private lateinit var photoIcon: ImageView
    private lateinit var aplicarAlteracoes: Button

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        userEmail = findViewById(R.id.emailInput)
        userJob = findViewById(R.id.jobInput)
        userPhoto = findViewById(R.id.user_photo)
        photoIcon = findViewById(R.id.icon_camera)
        aplicarAlteracoes = findViewById(R.id.saveEditProfileButton)

        photoIcon.setOnClickListener{
            openFileChooser()
        }

        aplicarAlteracoes.setOnClickListener {
            if (imageUri != null){
                uploadImageToFirebaseStorage()
            }
        }

        fetchUserDetails()
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri).into(userPhoto)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        imageUri?.let { uri ->
            val user = FirebaseAuth.getInstance().currentUser?.email
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$user")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val userRef = FirebaseFirestore.getInstance().collection("users").document(user!!)

                        userRef.update("profileImageUrl", downloadUrl.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Fotografia atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro!!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }

    private fun fetchUserDetails(){
        val user = FirebaseAuth.getInstance().currentUser?.email
        if (user != null) {
            val firestore = FirebaseFirestore.getInstance()

            // Buscar informação da base de dados
            firestore.collection("users").document(user)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userEmail.text = document.getString("email") ?: "Email não encontrado"
                        userJob.text = document.getString("job") ?: "Cargo não encontrado"
                    }
                }
        }
    }
}