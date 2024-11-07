import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.squareup.picasso.Picasso
import ly.roast.roastly.R
import ly.roast.roastly.databinding.FragmentLeaderboardsBinding
import ly.roast.roastly.utils.TopEmployeeAdapter

class LeaderboardsFragment : Fragment() {
    private var _binding: FragmentLeaderboardsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaderboardsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        binding.scrollView.visibility = View.GONE

        viewModel.allDataLoaded.observe(viewLifecycleOwner) { isLoaded ->
            if (isLoaded) {
                binding.progressBar.visibility = View.GONE
                binding.scrollView.visibility = View.VISIBLE
            }
        }

        viewModel.topInitiativeUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsIniciativeUsername.text = it.name
                binding.ratingIniciativa.rating = it.averageIniciativa
                loadImage(it.profileImageUrl, binding.leaderboardsInitiativeImage)
            }
        }

        viewModel.topCollaborationUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsCollaborationUsername.text = it.name
                binding.ratingColaboracao.rating = it.averageColaboracao
                loadImage(it.profileImageUrl, binding.leaderboardsCollaborationImage)
            }
        }

        viewModel.topKnowledgeUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsKnowledgeUsername.text = it.name
                binding.ratingConhecimento.rating = it.averageConhecimento
                loadImage(it.profileImageUrl, binding.leaderboardsCommitmentImage)
            }
        }

        viewModel.topResponsibilityUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsResponsibilityUsername.text = it.name
                binding.ratingResponsabilidade.rating = it.averageResponsabilidade
                loadImage(it.profileImageUrl, binding.leaderboardsCommunicationImage)
            }
        }

        viewModel.topOverallUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.allCategoriesName.text = it.name
                binding.allCategoriesRating.text = String.format("%.1f", it.averageOverall)
                loadImage(it.profileImageUrl, binding.allCategoriesImage)
            }
        }

        viewModel.topEmployeeOfMonthUsers.observe(viewLifecycleOwner) { users ->
            users?.let {
                binding.bestEmployeesRecyclerView.adapter = TopEmployeeAdapter(it)
            }
        }
    }

    private fun loadImage(url: String?, imageView: ImageView) {
        if (!url.isNullOrEmpty()) {
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.profile_default_image)
                .error(R.drawable.profile_default_image)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.profile_default_image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
