package ly.roast.roastly.ui.review

import AddFragmentViewModel
import User
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
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
    private lateinit var commentBox: EditText
    private lateinit var submitButton: Button

    private val viewModel: AddFragmentViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_user_review, container, false)

        selectedUser = arguments?.getParcelable("selectedUser")

        ratingIniciativa = view.findViewById(R.id.rating_iniciativa)
        ratingConhecimento = view.findViewById(R.id.rating_conhecimento)
        ratingColaboracao = view.findViewById(R.id.rating_colaboracao)
        ratingResponsabilidade = view.findViewById(R.id.rating_responsabilidade)
        commentBox = view.findViewById(R.id.comment_box)
        submitButton = view.findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            submitReview()
        }

        return view
    }

    private fun submitReview() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val comment = commentBox.text.toString()

        selectedUser?.let { user ->
            currentUser?.let { firebaseUser ->
                val userEmail = firebaseUser.email ?: "default@example.com"

                firestore.collection("users").document(userEmail).get()
                    .addOnSuccessListener { document ->
                        val reviewerName = document.getString("name") ?: "Anonymous"
                        val currentUserObject = User(
                            uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            name = reviewerName,
                            email = firebaseUser.email ?: ""
                        )

                        viewModel.submitReview(
                            currentUser = currentUserObject,
                            selectedUserUid = user.uid,
                            selectedUserName = user.name,
                            recipientEmail = user.email,
                            iniciativa = ratingIniciativa.rating,
                            conhecimento = ratingConhecimento.rating,
                            colaboracao = ratingColaboracao.rating,
                            responsabilidade = ratingResponsabilidade.rating,
                            comment = comment,
                            onSuccess = {
                                Toast.makeText(context, "Feedback enviado com sucesso!", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Falha ao enviar o feedback: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Erro aos buscar dados do utilizador: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
