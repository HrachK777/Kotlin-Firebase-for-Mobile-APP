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
        holder.feedText.text = "deu feedback a"
        holder.feedNameReceived.text = review.recipientName
        Log.d("Review", review.reviewedOn.toDate().toString())
        holder.feedDayTime.text = simplifyTimestamp(review.reviewedOn.toDate().toString())
    }

    fun simplifyTimestamp(originalTimestamp: String): String {
        val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        //val targetFormat = SimpleDateFormat("MMM dd - HH:mm", Locale.ENGLISH)

        return try {
            val reviewDate = originalFormat.parse(originalTimestamp)
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - reviewDate.time

            when {
                timeDifference < 60_000 -> "Agora mesmo"
                timeDifference < 2 * 60_000 -> "Há 1 minuto"
                timeDifference < 60 * 60_000 -> "Há ${timeDifference / 60_000} minutos"
                timeDifference < 2 * 60 * 60_000 -> "Há uma hora"
                timeDifference < 24 * 60 * 60_000 -> "Há ${timeDifference / (60 * 60_000)} horas"
                timeDifference < 2 * 24 * 60 * 60_000 -> "Ontem"
                timeDifference < 7 * 24 * 60 * 60_000 -> "Há ${timeDifference / (24 * 60 * 60_000)} dias"
                timeDifference < 30 * 24 * 60 * 60_000 -> "Há ${timeDifference / (7 * 24 * 60 * 60_000)} semanas"
                timeDifference < 365 * 24 * 60 * 60_000 -> "Há ${timeDifference / (30 * 24 * 60 * 60_000)} meses"
                timeDifference < 2 * 365 * 24 * 60 * 60_000 -> "Há 1 ano"
                else -> "Há ${timeDifference / (365 * 24 * 60 * 60_000)} anos"
            }
        } catch (e: Exception) {
            originalTimestamp
        }

        /*return try {
            val date = originalFormat.parse(originalTimestamp)
            targetFormat.format(date)
        } catch (e: Exception) {
            originalTimestamp
        }*/
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
