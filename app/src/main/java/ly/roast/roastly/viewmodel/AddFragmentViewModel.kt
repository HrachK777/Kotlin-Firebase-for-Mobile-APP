import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class AddFragmentViewModel : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users
    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchUsersFromFirestore()
    }

    fun fetchUsersFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val availableUsers = mutableListOf<User>()

                for (document in result) {
                    if (document.id != currentUserId) {
                        val user = document.toObject(User::class.java).copy(uid = document.id)

                        firestore.collection("users")
                            .document(user.uid)
                            .collection("reviews")
                            .whereEqualTo("reviewerId", currentUserId)
                            .get()
                            .addOnSuccessListener { reviewResult ->

                                var alreadyReviewedThisMonth = false

                                for (review in reviewResult.documents) {
                                    val reviewedOn = review.getTimestamp("reviewedOn")?.toDate()

                                    if (reviewedOn != null) {
                                        val calendar = Calendar.getInstance()
                                        calendar.time = reviewedOn

                                        if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(
                                                Calendar.YEAR
                                            ) == currentYear
                                        ) {
                                            alreadyReviewedThisMonth = true
                                            break
                                        }
                                    }
                                }

                                if (!alreadyReviewedThisMonth) {
                                    availableUsers.add(user)
                                }
                                _users.value = availableUsers
                            }
                    }
                }
            }
    }

    fun submitReview(
        currentUser: User,
        selectedUserUid: String,
        iniciativa: Float,
        conhecimento: Float,
        colaboracao: Float,
        responsabilidade: Float,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val reviewData = hashMapOf(
            "reviewerId" to currentUser.uid,
            "reviewerName" to currentUser.name,
            "iniciativa" to iniciativa,
            "conhecimento" to conhecimento,
            "colaboracao" to colaboracao,
            "responsabilidade" to responsabilidade,
            "reviewedOn" to Timestamp.now()
        )

        firestore.collection("users")
            .document(selectedUserUid)
            .collection("reviews")
            .add(reviewData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
