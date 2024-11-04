import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import ly.roast.roastly.R
import ly.roast.roastly.databinding.FragmentFeedbacksHistoryBinding

class FeedbackHistoryFragment : Fragment() {

    private var _binding: FragmentFeedbacksHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedbacksHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedbackPagerAdapter = FeedbackPagerAdapter(requireActivity())
        binding.viewPager.adapter = feedbackPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Feedbacks Recebidos" else "Feedbacks Dados"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
