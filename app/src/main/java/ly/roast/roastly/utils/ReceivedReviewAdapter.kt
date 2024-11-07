import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ly.roast.roastly.R
import java.util.Locale

class ReceivedReviewAdapter(private val onClick: (Review) -> Unit) :
    RecyclerView.Adapter<ReceivedReviewAdapter.ViewHolder>() {

    private val reviews = mutableListOf<Review>()

    fun submitList(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.historic_received_feedback_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]

        holder.reviewerName.text = review.reviewerName
        holder.timestamp.text = simplifyTimestamp(review.reviewedOn.toDate().toString())

        if (review.recipientProfileImageUrl.isNotEmpty()) {
            Picasso.get().load(review.reviewerProfileImageUrl).into(holder.profileImage)
        } else {
            holder.profileImage.setImageResource(R.drawable.profile_default_image) // Default image
        }
    }

    override fun getItemCount(): Int = reviews.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val reviewerName: TextView = view.findViewById(R.id.received_name_received)
        val timestamp: TextView = view.findViewById(R.id.received_day_time)
        val profileImage: ImageView = view.findViewById(R.id.profile_image_received) // Reference to profile image

        init {
            view.setOnClickListener {
                onClick(reviews[adapterPosition])
            }
        }
    }
}
