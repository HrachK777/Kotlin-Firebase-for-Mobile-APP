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

    // Add loading flags for each dataset
    private var initiativeLoaded = false
    private var collaborationLoaded = false
    private var knowledgeLoaded = false
    private var responsibilityLoaded = false
    private var overallLoaded = false
    private var employeeOfMonthLoaded = false

    private val _allDataLoaded = MutableLiveData<Boolean>()
    val allDataLoaded: LiveData<Boolean> get() = _allDataLoaded

    init {
        fetchTopUsers()
    }

    private fun fetchTopUsers() {
        fetchTopUserInField("averageIniciativa", _topInitiativeUser) { initiativeLoaded = true; checkAllDataLoaded() }
        fetchTopUserInField("averageColaboracao", _topCollaborationUser) { collaborationLoaded = true; checkAllDataLoaded() }
        fetchTopUserInField("averageConhecimento", _topKnowledgeUser) { knowledgeLoaded = true; checkAllDataLoaded() }
        fetchTopUserInField("averageResponsabilidade", _topResponsibilityUser) { responsibilityLoaded = true; checkAllDataLoaded() }
        fetchTopUserInField("averageOverall", _topOverallUser) { overallLoaded = true; checkAllDataLoaded() }

        firestore.collection("users")
            .orderBy("employeeOfTheMonthWins", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                _topEmployeeOfMonthUsers.value = result.documents.mapNotNull { it.toObject(User::class.java) }
                employeeOfMonthLoaded = true
                checkAllDataLoaded()
            }
    }

    private fun fetchTopUserInField(field: String, liveData: MutableLiveData<User>, onLoadComplete: () -> Unit) {
        firestore.collection("users")
            .orderBy(field, Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val user = result.documents.firstOrNull()?.toObject(User::class.java)
                liveData.value = user
                onLoadComplete()
            }
            .addOnFailureListener { exception ->
                Log.e("LeaderboardsViewModel", "Error fetching top user in $field", exception)
                onLoadComplete()
            }
    }

    private fun checkAllDataLoaded() {
        _allDataLoaded.value = initiativeLoaded && collaborationLoaded && knowledgeLoaded &&
                responsibilityLoaded && overallLoaded && employeeOfMonthLoaded
    }
}
