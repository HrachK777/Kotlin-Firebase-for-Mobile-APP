import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistoryViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _givenReviews = MutableLiveData<List<Review>>()
    val givenReviews: LiveData<List<Review>> get() = _givenReviews

    private val _receivedReviews = MutableLiveData<List<Review>>()
    val receivedReviews: LiveData<List<Review>> get() = _receivedReviews

    fun fetchGivenReviews(currentUserId: String) {
        firestore.collectionGroup("reviews")
            .whereEqualTo("reviewerId", currentUserId)
            .orderBy("reviewedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val reviewList = mutableListOf<Review>()
                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    reviewList.add(review)
                }
                _givenReviews.value = reviewList
            }
            .addOnFailureListener { exception ->
                Log.e("FetchGivenReviews", "Error fetching given reviews", exception)
            }
    }

    fun fetchReceivedReviews(currentUserId: String) {
        firestore.collectionGroup("reviews")
            .whereEqualTo("recipientId", currentUserId)
            .orderBy("reviewedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val reviewList = mutableListOf<Review>()
                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    reviewList.add(review)
                }
                _receivedReviews.value = reviewList
            }
            .addOnFailureListener { exception ->
                Log.e("FetchReceivedReviews", "Error fetching received reviews", exception)
            }
    }
}
