import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import ly.roast.roastly.R

class HistoricGivenFragment : Fragment() {

    private lateinit var givenReviewAdapter: GivenReviewAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historic_given, container, false)

        recyclerView = view.findViewById(R.id.given_feedback_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        givenReviewAdapter = GivenReviewAdapter { review ->
            openReviewDetails(review)
        }
        recyclerView.adapter = givenReviewAdapter

        observeViewModel()
        FirebaseAuth.getInstance().currentUser?.email?.let { currentUserEmail ->
            viewModel.fetchGivenReviews(currentUserEmail)
        } ?: run {
            Log.e("HistoricGivenFragment", "User email not found.")
        }
        return view
    }

    private fun observeViewModel() {
        viewModel.givenReviews.observe(viewLifecycleOwner) { reviewList ->
            givenReviewAdapter.submitList(reviewList)
        }
    }

    private fun openReviewDetails(review: Review) {
        val reviewDetailsFragment = ReviewDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable("review", review)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, reviewDetailsFragment)
            .addToBackStack(null)
            .commit()
    }
}
