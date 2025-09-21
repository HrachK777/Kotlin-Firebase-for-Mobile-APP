import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistoryViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _givenReviews = MutableLiveData<List<Review>>()
    val givenReviews: LiveData<List<Review>> get() = _givenReviews

    private val _receivedReviews = MutableLiveData<List<Review>>()
    val receivedReviews: LiveData<List<Review>> get() = _receivedReviews

    fun fetchGivenReviews(currentUserEmail: String) {
        firestore.collectionGroup("reviews")
            .whereEqualTo("reviewerEmail", currentUserEmail)
            .orderBy("reviewedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val reviewList = mutableListOf<Review>()
                val emailToImageUrl = mutableMapOf<String, String>()

                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    reviewList.add(review)
                    if (review.recipientProfileImageUrl.isEmpty()) {
                        emailToImageUrl[review.recipientEmail] = ""
                    }
                }

                val tasks = emailToImageUrl.keys.map { email ->
                    firestore.collection("users").document(email).get()
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    for (task in tasks) {
                        if (task.isSuccessful) {
                            val document = task.result
                            val email = document.id
                            val profileUrl = document.getString("profileImageUrl") ?: ""
                            emailToImageUrl[email] = profileUrl
                        }
                    }

                    reviewList.forEach { review ->
                        review.recipientProfileImageUrl = emailToImageUrl[review.recipientEmail] ?: ""
                    }

                    _givenReviews.value = reviewList
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FetchGivenReviews", "Error fetching given reviews", exception)
            }
    }

    fun fetchReceivedReviews(currentUserEmail: String) {
        firestore.collectionGroup("reviews")
            .whereEqualTo("recipientEmail", currentUserEmail)
            .orderBy("reviewedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val reviewList = mutableListOf<Review>()
                val emailToImageUrl = mutableMapOf<String, String>()

                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    reviewList.add(review)
                    if (review.reviewerProfileImageUrl.isEmpty()) {
                        emailToImageUrl[review.reviewerEmail] = ""
                    }
                }

                val tasks = emailToImageUrl.keys.map { email ->
                    firestore.collection("users").document(email).get()
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener {
                    for (task in tasks) {
                        if (task.isSuccessful) {
                            val document = task.result
                            val email = document.id
                            val profileUrl = document.getString("profileImageUrl") ?: ""
                            emailToImageUrl[email] = profileUrl
                        }
                    }

                    reviewList.forEach { review ->
                        review.reviewerProfileImageUrl = emailToImageUrl[review.reviewerEmail] ?: ""
                    }

                    _receivedReviews.value = reviewList
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FetchReceivedReviews", "Error fetching received reviews", exception)
            }
    }
}
