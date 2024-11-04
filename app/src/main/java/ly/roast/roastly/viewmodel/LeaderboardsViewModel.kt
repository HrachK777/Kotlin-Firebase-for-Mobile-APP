import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _topInitiativeUser = MutableLiveData<User>()
    val topInitiativeUser: LiveData<User> get() = _topInitiativeUser

    private val _topCollaborationUser = MutableLiveData<User>()
    val topCollaborationUser: LiveData<User> get() = _topCollaborationUser

    private val _topKnowledgeUser = MutableLiveData<User>()
    val topKnowledgeUser: LiveData<User> get() = _topKnowledgeUser

    private val _topResponsibilityUser = MutableLiveData<User>()
    val topResponsibilityUser: LiveData<User> get() = _topResponsibilityUser

    private val _topOverallUser = MutableLiveData<User>()
    val topOverallUser: LiveData<User> get() = _topOverallUser

    private val _topEmployeeOfMonthUsers = MutableLiveData<List<User>>()
    val topEmployeeOfMonthUsers: LiveData<List<User>> get() = _topEmployeeOfMonthUsers

    init {
        fetchTopUsers()
    }

    private fun fetchTopUsers() {
        fetchTopUserInField("averageIniciativa", _topInitiativeUser)
        fetchTopUserInField("averageColaboracao", _topCollaborationUser)
        fetchTopUserInField("averageConhecimento", _topKnowledgeUser)
        fetchTopUserInField("averageResponsabilidade", _topResponsibilityUser)
        fetchTopUserInField("averageOverall", _topOverallUser)

        firestore.collection("users")
            .orderBy("employeeOfTheMonthWins", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                _topEmployeeOfMonthUsers.value =
                    result.documents.mapNotNull { it.toObject(User::class.java) }
            }
    }

    private fun fetchTopUserInField(field: String, liveData: MutableLiveData<User>) {
        Log.d("LeaderboardsViewModel", "Starting fetchTopUserInField() for field: $field")

        firestore.collection("users")
            .orderBy(field, Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val user = result.documents.firstOrNull()?.toObject(User::class.java)
                liveData.value = user
            }
            .addOnFailureListener { exception ->
            }
    }
}
