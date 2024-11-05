import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import ly.roast.roastly.R
import java.util.Locale

@Parcelize
data class Review(
    val reviewerName: String = "",
    val recipientName: String = "",
    val iniciativa: Float = 0f,
    val conhecimento: Float = 0f,
    val colaboracao: Float = 0f,
    val responsabilidade: Float = 0f,
    val comment: String = "",
    val reviewedOn: Timestamp = Timestamp(11,11)
) : Parcelable

class FeedAdapter(
    private var reviews: List<Review>,
    private val onClick: (Review) -> Unit
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val feedNameGiven: TextView = view.findViewById(R.id.feed_name_given)
        val feedText: TextView = view.findViewById(R.id.feed_text)
        val feedNameReceived: TextView = view.findViewById(R.id.feed_name_received)
        val feedDayTime: TextView = view.findViewById(R.id.feed_day_time)

        init {
            view.setOnClickListener {
                onClick(reviews[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_given_card_view, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val review = reviews[position]
        holder.feedNameGiven.text = review.reviewerName
        holder.feedText.text = "gave feedback to"
        holder.feedNameReceived.text = review.recipientName
        Log.d("Review", review.reviewedOn.toDate().toString())
        holder.feedDayTime.text = simplifyTimestamp(review.reviewedOn.toDate().toString())
    }

    fun simplifyTimestamp(originalTimestamp: String): String {
        val originalFormat = SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.ENGLISH)
        val targetFormat = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.ENGLISH)

        return try {
            val date = originalFormat.parse(originalTimestamp)
            targetFormat.format(date)
        } catch (e: Exception) {
            originalTimestamp
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
