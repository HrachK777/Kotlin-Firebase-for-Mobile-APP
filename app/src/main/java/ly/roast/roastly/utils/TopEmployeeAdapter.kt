package ly.roast.roastly.utils

import User
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ly.roast.roastly.R

class TopEmployeeAdapter(
    private var users: List<User>
) : RecyclerView.Adapter<TopEmployeeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.employee_name)
        val winsTextView: TextView = view.findViewById(R.id.highlight_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_employee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.nameTextView.text = user.name
        holder.winsTextView.text = user.employeeOfTheMonthWins.toString()
        Log.d("TopEmployeeAdapter", "Binding user: ${user.name}")
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        Log.d("TopEmployeeAdapter", "Updating users list: ${newUsers.size}")
        users = newUsers
        notifyDataSetChanged()
    }
}