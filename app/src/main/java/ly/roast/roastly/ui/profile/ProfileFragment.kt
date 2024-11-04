package ly.roast.roastly.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.R

class ProfileFragment : Fragment() {

    private lateinit var userNameLogin: TextView
    private lateinit var jobNameLogin: TextView
    private var feedbackGivenTotal: Int = 0
    private var feedbackReceivedTotal: Int = 0
    private lateinit var feedbackGivenTotalText: TextView
    private lateinit var feedbackReceivedTotalText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)
        
        userNameLogin = view.findViewById(R.id.user_name)
        jobNameLogin = view.findViewById(R.id.job_name)
        feedbackGivenTotalText = view.findViewById(R.id.text_profile_feedback_received)
        feedbackReceivedTotalText = view.findViewById(R.id.text_profile_feedback_given)

        fetchUserProfileData()
        fetchUserFeedbacks()

        return view
    }

    private fun fetchUserProfileData() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            val firestore = FirebaseFirestore.getInstance()

            // Buscar informação da base de dados
            firestore.collection("users").document(userEmail)
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

    private fun fetchUserFeedbacks(){
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        FirebaseFirestore.getInstance().collection("users")
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    feedbackGivenTotal = document.getLong("feedbacksGiven")?.toInt() ?: 0
                    feedbackReceivedTotal = document.getLong("feedbacksReceived")?.toInt() ?: 0
                    feedbackReceivedTotalText.text = "Feedbacks Recebidos: " + feedbackReceivedTotal.toString()
                    feedbackGivenTotalText.text = "Feedbacks Efetuados: " + feedbackGivenTotal.toString()
                }
            }
            .addOnFailureListener { exception ->

            }
    }
}
