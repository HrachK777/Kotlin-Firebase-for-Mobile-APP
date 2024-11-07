import android.icu.text.SimpleDateFormat
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize
import ly.roast.roastly.R
import java.util.Locale

@Parcelize
data class Review(
    val reviewerEmail: String = "",
    val reviewerName: String = "",
    val recipientEmail: String = "",
    val recipientName: String = "",
    val iniciativa: Float = 0f,
    var reviewerProfileImageUrl: String = "",
    var recipientProfileImageUrl: String = "",
    val conhecimento: Float = 0f,
    val colaboracao: Float = 0f,
    val responsabilidade: Float = 0f,
    val comment: String = "",
    val reviewedOn: Timestamp = Timestamp(11, 11),
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
        val profileImageGiven: ImageView = view.findViewById(R.id.profile_image_given)
        val profileImageReceived: ImageView = view.findViewById(R.id.profile_image_received)

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

        holder.feedDayTime.text = simplifyTimestamp(review.reviewedOn.toDate().toString())

        if (review.reviewerProfileImageUrl.isNotEmpty()) {
            Picasso.get().load(review.reviewerProfileImageUrl).into(holder.profileImageGiven, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    Log.d("PicassoSuccess", "Loaded reviewer image: ${review.reviewerProfileImageUrl}")
                }

                override fun onError(e: Exception?) {
                    Log.e("PicassoError", "Error loading reviewer image: ${review.reviewerProfileImageUrl}", e)
                }
            })
        } else {
            holder.profileImageGiven.setImageResource(R.drawable.profile_default_image)
            Log.d("PicassoDefaultImage", "Set default reviewer image for: ${review.reviewerName}")
        }

        if (review.recipientProfileImageUrl.isNotEmpty()) {
            Picasso.get().load(review.recipientProfileImageUrl).into(holder.profileImageReceived, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    Log.d("PicassoSuccess", "Loaded recipient image: ${review.recipientProfileImageUrl}")
                }

                override fun onError(e: Exception?) {
                    Log.e("PicassoError", "Error loading recipient image: ${review.recipientProfileImageUrl}", e)
                }
            })
        } else {
            holder.profileImageReceived.setImageResource(R.drawable.profile_default_image)
            Log.d("PicassoDefaultImage", "Set default recipient image for: ${review.recipientName}")
        }
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
