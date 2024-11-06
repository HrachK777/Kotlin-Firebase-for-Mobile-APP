package ly.roast.roastly.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.R
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {

    private lateinit var userNameLogin: TextView
    private lateinit var jobNameLogin: TextView
    private lateinit var feedbackGivenTotalText: TextView
    private lateinit var feedbackReceivedTotalText: TextView
    private lateinit var oneStarImage: ImageView
    private lateinit var twoStarImage: ImageView
    private lateinit var threeStarImage: ImageView
    private lateinit var fourStarImage: ImageView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var profileViewModel: LeaderboardsViewModel.ProfileViewModel
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)

        // Inicialização das Views
        val starIniciativa = view.findViewById<ImageView>(R.id.one_star_image)
        val starColaboracao = view.findViewById<ImageView>(R.id.two_star_image)
        val starConhecimento = view.findViewById<ImageView>(R.id.three_star_image)
        val starResponsabilidade = view.findViewById<ImageView>(R.id.four_star_image)

        profileViewModel = ViewModelProvider(this).get(LeaderboardsViewModel.ProfileViewModel::class.java)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            setStarImage(starIniciativa, user.averageIniciativa)
            setStarImage(starColaboracao, user.averageColaboracao)
            setStarImage(starConhecimento, user.averageConhecimento)
            setStarImage(starResponsabilidade, user.averageResponsabilidade)
        }

        userNameLogin = view.findViewById(R.id.user_name)
        jobNameLogin = view.findViewById(R.id.job_name)
        feedbackGivenTotalText = view.findViewById(R.id.text_profile_feedback_given)
        feedbackReceivedTotalText = view.findViewById(R.id.text_profile_feedback_received)
        oneStarImage = view.findViewById(R.id.one_star_image)
        twoStarImage = view.findViewById(R.id.two_star_image)
        threeStarImage = view.findViewById(R.id.three_star_image)
        fourStarImage = view.findViewById(R.id.four_star_image)

        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.email ?: return view

        // Fetch user data and feedbacks
        fetchUserProfileData()
        fetchUserFeedbacks()
        fetchUserData()

        profileViewModel.loadUserProfile(userId)

        return view
    }

    private fun fetchUserProfileData() {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userNameLogin.text = document.getString("name") ?: "Nome não disponível"
                    jobNameLogin.text = document.getString("job") ?: "Cargo não disponível"
                } else {
                    Log.d("ProfileFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting documents: ", exception)
                userNameLogin.text = "Erro ao carregar o nome"
                jobNameLogin.text = "Erro ao carregar o cargo"
            }
    }

    private fun fetchUserFeedbacks() {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val feedbackGivenTotal = document.getLong("feedbacksGiven")?.toInt() ?: 0
                    val feedbackReceivedTotal = document.getLong("feedbacksReceived")?.toInt() ?: 0
                    feedbackReceivedTotalText.text = "Feedbacks Recebidos: $feedbackReceivedTotal"
                    feedbackGivenTotalText.text = "Feedbacks Efetuados: $feedbackGivenTotal"
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting documents: ", exception)
                Toast.makeText(requireContext(), "Erro ao recuperar feedbacks", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun fetchUserData() {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val averageColaboracao = document.getDouble("averageColaboracao")?.toInt() ?: 0
                    val averageIniciativa = document.getDouble("averageIniciativa")?.toInt() ?: 0
                    val averageResponsabilidade = document.getDouble("averageResponsabilidade")?.toInt() ?: 0
                    val averageCompromisso = document.getDouble("averageConhecimento")?.toInt() ?: 0
                    updateStars(
                        averageColaboracao,
                        averageIniciativa,
                        averageResponsabilidade,
                        averageCompromisso
                    )
                } else {
                    Log.d("ProfileFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting documents: ", exception)
                Toast.makeText(requireContext(), "Erro ao recuperar dados", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateStars(
        colaboracao: Int,
        iniciativa: Int,
        responsabilidade: Int,
        compromisso: Int
    ) {
        oneStarImage.setImageResource(getStarImage(iniciativa))
        twoStarImage.setImageResource(getStarImage(colaboracao))
        threeStarImage.setImageResource(getStarImage(compromisso))
        fourStarImage.setImageResource(getStarImage(responsabilidade))
    }

    private fun getStarImage(rating: Int): Int {
        return when (rating) {
            1 -> R.drawable.one_stars_profile_card
            2 -> R.drawable.two_stars_profile_card
            3 -> R.drawable.three_stars_profile_card
            4 -> R.drawable.four_stars_profile_card
            5 -> R.drawable.five_stars_profile_card
            else -> R.drawable.one_stars_profile_card
        }
    }

    private fun setStarImage(imageView: ImageView, rating: Float) {
        val roundedRating = rating.roundToInt()
        val drawableRes = getStarImage(roundedRating)
        imageView.setImageResource(drawableRes)
    }
}
