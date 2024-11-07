package ly.roast.roastly.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import ly.roast.roastly.R
import ly.roast.roastly.viewmodel.ProfileViewModel
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
    private lateinit var userId: String
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userPhoto: ImageView
    private lateinit var scrollView: ScrollView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)

        userNameLogin = view.findViewById(R.id.user_name)
        jobNameLogin = view.findViewById(R.id.job_name)
        feedbackGivenTotalText = view.findViewById(R.id.text_profile_feedback_given)
        feedbackReceivedTotalText = view.findViewById(R.id.text_profile_feedback_received)
        oneStarImage = view.findViewById(R.id.one_star_image)
        twoStarImage = view.findViewById(R.id.two_star_image)
        threeStarImage = view.findViewById(R.id.three_star_image)
        fourStarImage = view.findViewById(R.id.four_star_image)
        userPhoto = view.findViewById(R.id.user_photo)
        scrollView = view.findViewById(R.id.scrollViewProfile)
        progressBar = view.findViewById(R.id.progress_bar)


        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.email ?: return view

        progressBar.visibility = View.VISIBLE
        userNameLogin.visibility = View.GONE
        jobNameLogin.visibility = View.GONE
        userPhoto.visibility = View.GONE
        scrollView.visibility = View.GONE
        fetchUserFeedbacks()
        fetchUserData()

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.loadUserProfile(userId)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            setStarImage(oneStarImage, user.averageIniciativa)
            setStarImage(twoStarImage, user.averageColaboracao)
            setStarImage(threeStarImage, user.averageConhecimento)
            setStarImage(fourStarImage, user.averageResponsabilidade)
            progressBar.visibility = View.GONE
            userNameLogin.visibility = View.VISIBLE
            jobNameLogin.visibility = View.VISIBLE
            userPhoto.visibility = View.VISIBLE
            scrollView.visibility = View.VISIBLE
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        fetchUserProfileData()
    }

    private fun fetchUserProfileData() {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userNameLogin.text = document.getString("name") ?: "Nome não disponível"
                    jobNameLogin.text = document.getString("job") ?: "Cargo não disponível"

                    val profileImageUrl = document.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(profileImageUrl).into(userPhoto)
                    } else {
                        userPhoto.setImageResource(R.drawable.profile_default_image)
                    }
                } else {
                    Log.d("ProfileFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting documents: ", exception)
                userNameLogin.text = "Erro ao carregar o nome"
                jobNameLogin.text = "Erro ao carregar o cargo"
                Toast.makeText(requireContext(), "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Erro ao recuperar feedbacks", Toast.LENGTH_SHORT).show()
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
                    val averageConhecimento = document.getDouble("averageConhecimento")?.toInt() ?: 0
                    updateStars(averageColaboracao, averageIniciativa, averageResponsabilidade, averageConhecimento)
                } else {
                    Log.d("ProfileFragment", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting documents: ", exception)
                Toast.makeText(requireContext(), "Erro ao recuperar dados", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateStars(colaboracao: Int, iniciativa: Int, responsabilidade: Int, compromisso: Int) {
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
            else -> R.drawable.one_stars_profile_card // Para zero ou valores inválidos
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