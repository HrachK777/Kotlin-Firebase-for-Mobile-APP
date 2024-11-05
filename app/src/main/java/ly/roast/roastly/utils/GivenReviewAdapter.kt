import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ly.roast.roastly.R
import java.util.Locale

class GivenReviewAdapter(private val onClick: (Review) -> Unit) :
    RecyclerView.Adapter<GivenReviewAdapter.ViewHolder>() {

    private val reviews = mutableListOf<Review>()

    fun submitList(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.historic_given_feedback_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        holder.recipientName.text = review.recipientName
        holder.timestamp.text = simplifyTimestamp(review.reviewedOn.toString())
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

    override fun getItemCount(): Int = reviews.size

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val recipientName: TextView = view.findViewById(R.id.given_name_given)
        val timestamp: TextView = view.findViewById(R.id.given_day_time)

        init {
            view.setOnClickListener {
                onClick(reviews[adapterPosition])
            }
        }
    }
}
