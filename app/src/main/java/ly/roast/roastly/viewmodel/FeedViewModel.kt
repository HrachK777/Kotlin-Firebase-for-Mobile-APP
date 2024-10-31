package ly.roast.roastly.viewmodel

import Review
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
                for (document in result) {
                    val review = document.toObject(Review::class.java)
                    reviewList.add(review)
                }
                _reviews.value = reviewList
            }.addOnFailureListener { exception ->
                Log.e("FetchReviews", "Error fetching reviews", exception)
            }
    }
}