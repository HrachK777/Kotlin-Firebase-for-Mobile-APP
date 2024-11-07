import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ly.roast.roastly.R
import ly.roast.roastly.databinding.FragmentFeedbacksFeedBinding

class ReviewFeedFragment : Fragment() {

    private var _binding: FragmentFeedbacksFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var feedAdapter: FeedAdapter
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbacksFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = FeedAdapter(emptyList()) { review ->
            openReviewDetails(review)
        }
        binding.recyclerView.adapter = feedAdapter

        observeViewModel()
        viewModel.fetchReviews()
    }

    private fun observeViewModel() {
        viewModel.reviews.observe(viewLifecycleOwner) { reviewList ->
            if (reviewList != null) {
                feedAdapter.updateData(reviewList)
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
