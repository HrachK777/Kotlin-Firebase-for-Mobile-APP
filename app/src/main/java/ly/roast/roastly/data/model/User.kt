import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.parcelize.Parcelize
import ly.roast.roastly.R

@Parcelize
data class User(
    val uid: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val job: String = "",
    val employeeOfTheMonthWins: Int = 0,
    val averageIniciativa: Float = 0f,
    val averageConhecimento: Float = 0f,
    val averageColaboracao: Float = 0f,
    val averageResponsabilidade: Float = 0f,
    val averageOverall: Float = 0f,
    val feedbacksGiven: Int = 0,
    val feedbacksReceived: Int = 0,
    val profileImageUrl: String = ""
) : Parcelable

class UserAdapter(
    private var userList: List<User> = emptyList(),
    private val onUserClicked: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.profile_name)
        val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)

        fun bind(user: User) {
            nameTextView.text = user.name

            // Load profile image with Picasso
            if (user.profileImageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(user.profileImageUrl)
                    .placeholder(R.drawable.profile_default_image)
                    .error(R.drawable.profile_default_image)
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.profile_default_image)
            }

            itemView.setOnClickListener {
                onUserClicked(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_view, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newUsers: List<User>) {
        userList = newUsers
        notifyDataSetChanged()
    }
}
