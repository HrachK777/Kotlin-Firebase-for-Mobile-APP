package ly.roast.roastly.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.R
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {

    private lateinit var userNameLogin: TextView
    private lateinit var jobNameLogin: TextView
    private var feedbackGivenTotal: Int = 0
    private var feedbackReceivedTotal: Int = 0
    private lateinit var feedbackGivenTotalText: TextView
    private lateinit var feedbackReceivedTotalText: TextView
    private lateinit var profileViewModel: LeaderboardsViewModel.ProfileViewModel
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)

        val starIniciativa = view.findViewById<ImageView>(R.id.one_star_image)
        val starColaboracao = view.findViewById<ImageView>(R.id.two_star_image)
        val starConhecimento = view.findViewById<ImageView>(R.id.three_star_image)
        val starResponsabilidade = view.findViewById<ImageView>(R.id.four_star_image)
        //val starOverall = view.findViewById<ImageView>(R.id.star_overall)

        profileViewModel = ViewModelProvider(this).get(LeaderboardsViewModel.ProfileViewModel::class.java)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            setStarImage(starIniciativa, user.averageIniciativa)
            setStarImage(starColaboracao, user.averageColaboracao)
            setStarImage(starConhecimento, user.averageConhecimento)
            setStarImage(starResponsabilidade, user.averageResponsabilidade)
            //setStarImage(starOverall, user.averageOverall)
        }
        
        userNameLogin = view.findViewById(R.id.user_name)
        jobNameLogin = view.findViewById(R.id.job_name)
        feedbackGivenTotalText = view.findViewById(R.id.text_profile_feedback_received)
        feedbackReceivedTotalText = view.findViewById(R.id.text_profile_feedback_given)

        fetchUserProfileData()
        fetchUserFeedbacks()

        profileViewModel.loadUserProfile(userId)

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

    private fun setStarImage(imageView: ImageView, rating: Float) {
        val roundedRating = rating.roundToInt() // Arredonda para o inteiro mais próximo
        val drawableRes = when (roundedRating) {
            1 -> R.drawable.one_stars_profile_card
            2 -> R.drawable.two_stars_profile_card
            3 -> R.drawable.three_stars_profile_card
            4 -> R.drawable.four_stars_profile_card
            5 -> R.drawable.five_stars_profile_card
            else -> R.drawable.one_stars_profile_card // Imagem padrão se algo der errado
        }
        imageView.setImageResource(drawableRes)
    }
}
