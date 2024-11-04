import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ly.roast.roastly.utils.TopEmployeeAdapter
import ly.roast.roastly.databinding.FragmentLeaderboardsBinding

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

        viewModel.topInitiativeUser.observe(viewLifecycleOwner) { user ->
            user?.let { it ->
                binding.leaderboardsIniciativeUsername.text = it.name
                binding.ratingIniciativa.rating = it.averageIniciativa
            }
        }

        viewModel.topCollaborationUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsCollaborationUsername.text = it.name
                binding.ratingColaboracao.rating = it.averageColaboracao
            }
        }

        viewModel.topKnowledgeUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsKnowledgeUsername.text = it.name
                binding.ratingConhecimento.rating = it.averageConhecimento
            }
        }

        viewModel.topResponsibilityUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.leaderboardsResponsibilityUsername.text = it.name
                binding.ratingResponsabilidade.rating = it.averageResponsabilidade
            }
        }

        viewModel.topOverallUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.allCategoriesName.text = it.name
                binding.allCategoriesRating.text = it.averageOverall.toString()
            }
        }

        viewModel.topEmployeeOfMonthUsers.observe(viewLifecycleOwner) { users ->
            users?.let {
                binding.bestEmployeesRecyclerView.adapter = TopEmployeeAdapter(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
