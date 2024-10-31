import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ly.roast.roastly.R
import ly.roast.roastly.viewmodel.FeedViewModel

class ReviewFeedFragment : Fragment() {

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_feed, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_feed)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        feedAdapter = FeedAdapter(emptyList())
        recyclerView.adapter = feedAdapter

        observeViewModel()
        viewModel.fetchReviews()

        return view
    }

    private fun observeViewModel() {
        viewModel.reviews.observe(viewLifecycleOwner) { reviewList ->
            feedAdapter.updateData(reviewList)
        }
    }
}
