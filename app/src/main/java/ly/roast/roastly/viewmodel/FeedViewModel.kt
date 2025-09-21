import Review
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FeedViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    fun fetchReviews() {
        firestore.collectionGroup("reviews")
            .orderBy("reviewedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val reviewList = mutableListOf<Review>()
                val emailsToFetch = mutableSetOf<String>()

                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    reviewList.add(review)

                    if (review.reviewerProfileImageUrl.isEmpty()) emailsToFetch.add(review.reviewerEmail)
                    if (review.recipientProfileImageUrl.isEmpty()) emailsToFetch.add(review.recipientEmail)
                }

                val tasks = emailsToFetch.map { email ->
                    firestore.collection("users").document(email).get()
                }

                Tasks.whenAllComplete(tasks).addOnSuccessListener { taskResults ->
                    val emailToImageUrl = mutableMapOf<String, String>()

                    for (task in taskResults) {
                        if (task.isSuccessful) {
                            val document = task.result as? com.google.firebase.firestore.DocumentSnapshot
                            document?.let {
                                val email = it.id
                                val profileUrl = it.getString("profileImageUrl") ?: ""
                                emailToImageUrl[email] = profileUrl
                                Log.d("ProfileImageFetch", "Fetched profile image URL for $email: $profileUrl")
                            }
                        } else {
                            Log.e("ProfileImageFetch", "Failed to fetch profile image for a user.")
                        }
                    }

                    reviewList.forEach { review ->
                        review.reviewerProfileImageUrl = emailToImageUrl[review.reviewerEmail] ?: ""
                        review.recipientProfileImageUrl = emailToImageUrl[review.recipientEmail] ?: ""
                        Log.d(
                            "ReviewUpdate",
                            "Updated review for ${review.reviewerEmail} -> Reviewer Image URL: ${review.reviewerProfileImageUrl}, Recipient Image URL: ${review.recipientProfileImageUrl}"
                        )
                    }

                    _reviews.value = reviewList
                }.addOnFailureListener { exception ->
                    Log.e("FetchReviews", "Error fetching profile images", exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FetchReviews", "Error fetching reviews", exception)
            }
    }
}
