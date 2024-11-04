import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FeedbackPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoricReceivedFragment() // Fragment for feedbacks received
            1 -> HistoricGivenFragment() // Fragment for feedbacks given
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}