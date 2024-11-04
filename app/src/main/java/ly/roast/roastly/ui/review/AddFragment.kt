import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ly.roast.roastly.R
import ly.roast.roastly.ui.review.AddUserReviewFragment

class AddFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel: AddFragmentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        userAdapter = UserAdapter(emptyList()) { user ->
            openAddUserReviewFragment(user)
        }
        recyclerView.adapter = userAdapter

        observeViewModel()

        return view
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { userList ->
            userAdapter.updateData(userList)
        }
    }

    private fun openAddUserReviewFragment(user: User) {
        val reviewFragment = AddUserReviewFragment()

        val bundle = Bundle()
        bundle.putParcelable("selectedUser", user)
        reviewFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, reviewFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUsersFromFirestore()
    }
}
