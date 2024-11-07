import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
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
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val availableUsers = mutableListOf<User>()

        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.documents.mapNotNull { document ->
                    val user = document.toObject(User::class.java)?.copy(
                        uid = document.getString("uid") ?: document.id,
                        profileImageUrl = document.getString("profileImageUrl") ?: ""
                    ) ?: return@mapNotNull null

                    if (user.email != currentUserEmail) {
                        firestore.collection("users")
                            .document(user.email)
                            .collection("reviews")
                            .whereEqualTo("reviewerEmail", currentUserEmail)
                            .get()
                            .continueWith { reviewTask ->
                                if (reviewTask.isSuccessful) {
                                    val alreadyReviewedThisMonth = reviewTask.result?.documents?.any { review ->
                                        val reviewedOn = review.getTimestamp("reviewedOn")?.toDate()
                                        reviewedOn?.let {
                                            val calendar = Calendar.getInstance().apply { time = it }
                                            calendar.get(Calendar.MONTH) == currentMonth &&
                                                    calendar.get(Calendar.YEAR) == currentYear
                                        } ?: false
                                    } ?: false

                                    if (!alreadyReviewedThisMonth) {
                                        availableUsers.add(user)
                                    }
                                }
                            }
                    } else {
                        Tasks.forResult(null)
                    }
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    _users.value = availableUsers // Update LiveData once all tasks complete
                }.addOnFailureListener { exception ->
                    Log.e("FetchUsers", "Error fetching users", exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FetchUsers", "Error fetching users", exception)
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
            "reviewerEmail" to currentUser.email,
            "reviewerName" to (currentUser.name.takeIf { it.isNotEmpty() } ?: "Anonymous"),
            "recipientEmail" to recipientEmail,
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
                firestore.collection("users").document(currentUser.email)
                    .update("feedbacksGiven", currentUser.feedbacksGiven + 1)
                    .addOnFailureListener { exception ->
                        Log.e("SubmitReview", "Failed to update feedbacksGiven", exception)
                    }

                firestore.collection("users").document(recipientEmail).get()
                    .addOnSuccessListener { recipientDoc ->
                        val feedbacksReceived = (recipientDoc.getLong("feedbacksReceived")?.toInt() ?: 0) + 1

                        val averageIniciativa = updateAverage(
                            recipientDoc.getDouble("averageIniciativa")?.toFloat() ?: 0f,
                            iniciativa,
                            feedbacksReceived
                        )
                        val averageConhecimento = updateAverage(
                            recipientDoc.getDouble("averageConhecimento")?.toFloat() ?: 0f,
                            conhecimento,
                            feedbacksReceived
                        )
                        val averageColaboracao = updateAverage(
                            recipientDoc.getDouble("averageColaboracao")?.toFloat() ?: 0f,
                            colaboracao,
                            feedbacksReceived
                        )
                        val averageResponsabilidade = updateAverage(
                            recipientDoc.getDouble("averageResponsabilidade")?.toFloat() ?: 0f,
                            responsabilidade,
                            feedbacksReceived
                        )

                        val averageTotal = (averageIniciativa + averageConhecimento + averageColaboracao + averageResponsabilidade) / 4

                        firestore.collection("users").document(recipientEmail)
                            .update(
                                mapOf(
                                    "feedbacksReceived" to feedbacksReceived,
                                    "averageIniciativa" to averageIniciativa,
                                    "averageConhecimento" to averageConhecimento,
                                    "averageColaboracao" to averageColaboracao,
                                    "averageResponsabilidade" to averageResponsabilidade,
                                    "averageOverall" to averageTotal
                                )
                            )
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception -> onFailure(exception) }

                    }
                    .addOnFailureListener { exception -> onFailure(exception) }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
    private fun updateAverage(currentAverage: Float, newValue: Float, count: Int): Float {
        return ((currentAverage * (count - 1)) + newValue) / count
    }
}
