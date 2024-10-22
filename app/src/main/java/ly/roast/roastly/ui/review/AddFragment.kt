package ly.roast.roastly.ui.review

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ly.roast.roastly.R
import ly.roast.roastly.data.model.User
import ly.roast.roastly.data.model.UserAdapter

class AddFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<User>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        fetchUsersFromFirestore()

        return view
    }

    private fun fetchUsersFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()

                for (document in result) {
                    if (document.id != currentUserId) {
                        val user = document.toObject(User::class.java)
                        userList.add(user)
                    }
                }

                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting users: ", exception)
            }
    }
}
