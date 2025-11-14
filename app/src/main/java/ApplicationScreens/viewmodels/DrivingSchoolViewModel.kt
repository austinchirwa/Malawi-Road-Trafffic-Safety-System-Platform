package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Data class to represent a driving school document in Firestore
data class DrivingSchool(
    val id: String = "", // Document ID
    val name: String = ""
)

class DrivingSchoolViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _schools = MutableStateFlow<List<DrivingSchool>>(emptyList())
    val schools: StateFlow<List<DrivingSchool>> = _schools

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadDrivingSchools()
    }

    private fun loadDrivingSchools() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("driving_schools").get().await()
                _schools.value = snapshot.documents.mapNotNull { doc ->
                    // Manually mapping to include the document ID
                    doc.toObject(DrivingSchool::class.java)?.copy(id = doc.id)
                }
            } catch (e: Exception) {
                // Handle error, e.g., log it or show a message
                _schools.value = emptyList() // Clear list on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Local await function to avoid KTX dependency issues
    private suspend fun <T> Task<T>.await(): T {
        if (isComplete) {
            val e = exception
            return if (e == null) {
                if (isCanceled) throw java.util.concurrent.CancellationException("Task was cancelled.")
                else result
            } else {
                throw e
            }
        }

        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener { task ->
                val e = task.exception
                if (e == null) {
                    if (task.isCanceled) cont.cancel()
                    else cont.resume(task.result)
                } else {
                    cont.resumeWithException(e)
                }
            }
        }
    }
}
