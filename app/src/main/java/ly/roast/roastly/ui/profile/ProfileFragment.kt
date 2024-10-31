package ly.roast.roastly.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.EditProfileActivity
import ly.roast.roastly.R

class ProfileFragment : Fragment() {

    private lateinit var userNameLogin: TextView
    private lateinit var jobNameLogin: TextView
    private lateinit var editProfileButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        userNameLogin = view.findViewById(R.id.user_name)
        jobNameLogin = view.findViewById(R.id.job_name)
        editProfileButton = view.findViewById(R.id.button_edit_profile)

        fetchUserProfileData()

        editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun fetchUserProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val firestore = FirebaseFirestore.getInstance()

            // Buscar informação da base de dados
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        userNameLogin.text = document.getString("name")
                        jobNameLogin.text = document.getString("job")
                    }
                }
                .addOnFailureListener { exception ->
                    userNameLogin.text = "Erro ao carregar o nome"
                    jobNameLogin.text = "Erro ao carregar o cargo"
                }
        }
    }
}
