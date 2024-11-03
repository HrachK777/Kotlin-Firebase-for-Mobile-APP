import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale


class AddFragmentViewModel : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users
    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchUsersFromFirestore()
    }

    fun fetchUsersFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val availableUsers = mutableListOf<User>()

                for (document in result) {
                    val user = document.toObject(User::class.java).copy(
                        uid = document.getString("uid") ?: document.id
                    )

                    if (user.email != currentUserEmail) {

                        firestore.collection("users")
                            .document(user.email)
                            .collection("reviews")
                            .whereEqualTo("reviewerId", currentUserEmail)
                            .get()
                            .addOnSuccessListener { reviewResult ->
                                var alreadyReviewedThisMonth = false

                                for (review in reviewResult.documents) {
                                    val reviewedOn = review.getTimestamp("reviewedOn")?.toDate()
                                    if (reviewedOn != null) {
                                        val calendar = Calendar.getInstance()
                                        calendar.time = reviewedOn

                                        if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
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
        selectedUserName: String,
        recipientEmail: String,
        iniciativa: Float,
        conhecimento: Float,
        colaboracao: Float,
        responsabilidade: Float,
        comment: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val currentMonth = monthFormat.format(Calendar.getInstance().time)

        val documentId = "${currentUser.email}-${recipientEmail}-${currentMonth}"

        val formattedComment = "\"$comment\""

        val reviewData = hashMapOf(
            "reviewerId" to currentUser.uid,
            "reviewerName" to (currentUser.name.takeIf { it.isNotEmpty() } ?: "Anonymous"),
            "recipientId" to selectedUserUid,
            "recipientName" to selectedUserName,
            "iniciativa" to iniciativa,
            "conhecimento" to conhecimento,
            "colaboracao" to colaboracao,
            "responsabilidade" to responsabilidade,
            "comment" to formattedComment,
            "reviewedOn" to Timestamp.now()
        )

        firestore.collection("users")
            .document(recipientEmail)
            .collection("reviews")
            .document(documentId)
            .set(reviewData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
