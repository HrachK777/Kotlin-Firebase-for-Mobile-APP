package ly.roast.roastly.ui.review

import AddFragmentViewModel
import User
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.R

class AddUserReviewFragment : Fragment() {

    private var selectedUser: User? = null
    private lateinit var ratingIniciativa: RatingBar
    private lateinit var ratingConhecimento: RatingBar
    private lateinit var ratingColaboracao: RatingBar
    private lateinit var ratingResponsabilidade: RatingBar

    private val viewModel: AddFragmentViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_user_review, container, false)

        selectedUser = arguments?.getParcelable("selectedUser")

        val feedbackTitle = view.findViewById<TextView>(R.id.add_feedback_title)
        val submitButton = view.findViewById<Button>(R.id.submit_button)

        feedbackTitle.text = "Dar feedback a ${selectedUser?.name}"

        ratingIniciativa = view.findViewById(R.id.rating_iniciativa)
        ratingConhecimento = view.findViewById(R.id.rating_conhecimento)
        ratingColaboracao = view.findViewById(R.id.rating_colaboracao)
        ratingResponsabilidade = view.findViewById(R.id.rating_responsabilidade)

        submitButton.setOnClickListener {
            submitReview()
        }

        return view
    }

    private fun submitReview() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        selectedUser?.let { user ->
            currentUser?.let { firebaseUser ->
                val userId = firebaseUser.uid

                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val reviewerName = document.getString("name") ?: "Anonymous"
                        val currentUserObject = User(
                            uid = userId,
                            name = reviewerName,
                            email = firebaseUser.email ?: ""
                        )

                        viewModel.submitReview(
                            currentUser = currentUserObject,
                            selectedUserUid = user.uid,
                            selectedUserName = user.name,
                            iniciativa = ratingIniciativa.rating,
                            conhecimento = ratingConhecimento.rating,
                            colaboracao = ratingColaboracao.rating,
                            responsabilidade = ratingResponsabilidade.rating,
                            onSuccess = {
                                Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Failed to submit review: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
