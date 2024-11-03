import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import ly.roast.roastly.R

class ReviewDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_added_feedback, container, false)

        val review = arguments?.getParcelable<Review>("review")

        val reviewerNameTextView: TextView = view.findViewById(R.id.added_feedbacks_name_given)
        val recipientNameTextView: TextView = view.findViewById(R.id.added_feedbacks_name_received)
        val iniciativaRatingBar: RatingBar = view.findViewById(R.id.rating_iniciativa)
        val conhecimentoRatingBar: RatingBar = view.findViewById(R.id.rating_conhecimento)
        val colaboracaoRatingBar: RatingBar = view.findViewById(R.id.rating_colaboracao)
        val responsabilidadeRatingBar: RatingBar = view.findViewById(R.id.rating_responsabilidade)
        val commentBox: EditText = view.findViewById(R.id.comment_box)

        review?.let {
            reviewerNameTextView.text = it.reviewerName
            recipientNameTextView.text = it.recipientName
            iniciativaRatingBar.rating = it.iniciativa
            conhecimentoRatingBar.rating = it.conhecimento
            colaboracaoRatingBar.rating = it.colaboracao
            responsabilidadeRatingBar.rating = it.responsabilidade
            commentBox.setText(it.comment)
        }

        commentBox.isEnabled = false

        return view
    }
}
