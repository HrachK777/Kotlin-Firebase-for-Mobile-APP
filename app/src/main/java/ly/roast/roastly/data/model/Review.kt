import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize
import ly.roast.roastly.R

@Parcelize
data class Review(
    val reviewerName: String = "",
    val recipientName: String = "",
    val iniciativa: Float = 0f,
    val conhecimento: Float = 0f,
    val colaboracao: Float = 0f,
    val responsabilidade: Float = 0f,
    val comment: String = "",
    val timestamp: String = ""
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
        holder.feedText.text = "deu feedback a"
        holder.feedNameReceived.text = review.recipientName
        holder.feedDayTime.text = review.timestamp
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
