import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ly.roast.roastly.R

data class Review(
    val reviewerName: String = "",
    val recipientName: String = "",
    val iniciativa: Float = 0f,
    val conhecimento: Float = 0f,
    val colaboracao: Float = 0f,
    val responsabilidade: Float = 0f
)

class FeedAdapter(private var reviews: List<Review>) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    inner class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewText: TextView = view.findViewById(R.id.review_text)
        val reviewDetails: TextView = view.findViewById(R.id.review_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review_feed, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val review = reviews[position]

        holder.reviewText.text = "${review.reviewerName} rated ${review.recipientName}"

        holder.reviewDetails.text = "Iniciativa: ${review.iniciativa}, Conhecimento: ${review.conhecimento}, " +
                "Colaboração: ${review.colaboracao}, Responsabilidade: ${review.responsabilidade}"
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
