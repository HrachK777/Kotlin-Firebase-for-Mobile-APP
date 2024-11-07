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

class HistoricReceivedFragment : Fragment() {

    private lateinit var receivedReviewAdapter: ReceivedReviewAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel: HistoryViewModel by viewModels()
    private val currentUserID: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historic_received, container, false)

        recyclerView = view.findViewById(R.id.received_feedback_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        receivedReviewAdapter = ReceivedReviewAdapter { review ->
            openReviewDetails(review)
        }
        recyclerView.adapter = receivedReviewAdapter

        observeViewModel()
        FirebaseAuth.getInstance().currentUser?.email?.let { currentUserEmail ->
            viewModel.fetchReceivedReviews(currentUserEmail)
        } ?: run {
            Log.e("HistoricReceivedFragment", "User email not found.")
        }
        return view
    }

    private fun observeViewModel() {
        viewModel.receivedReviews.observe(viewLifecycleOwner) { reviewList ->
            receivedReviewAdapter.submitList(reviewList)
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
