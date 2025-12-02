package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class to represent an incident report.
 */
data class IncidentReport(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val reporterName: String = "",
    val contactNumber: String = "",
    val incidentType: String = "",
    val location: String = "",
    val description: String = "",
    val status: String = "Reported", // Reported, Investigating, Resolved
    val timestamp: Any = FieldValue.serverTimestamp(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Sealed class to represent the state of the report submission.
 */
sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    data class Success(val message: String = "Report sent successfully!") : ReportState()
    data class Error(val message: String) : ReportState()
}

class ReportIncidentViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val reportsCollection = db.collection("incident_reports")

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState

    /**
     * Submits an incident report to Firestore.
     */
    fun sendReport(report: IncidentReport) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            try {
                val user = auth.currentUser
                // We allow anonymous reporting if user is not logged in, but prefer logged in
                val userId = user?.uid ?: "anonymous"
                val userEmail = user?.email ?: "anonymous"

                val reportData = hashMapOf(
                    "userId" to userId,
                    "userEmail" to userEmail,
                    "reporterName" to report.reporterName,
                    "contactNumber" to report.contactNumber,
                    "incidentType" to report.incidentType,
                    "location" to report.location,
                    "description" to report.description,
                    "status" to "Reported",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "createdAt" to System.currentTimeMillis()
                )

                val docRef = reportsCollection.add(reportData).await()
                
                _reportState.value = ReportState.Success("Report submitted successfully! Reference: ${docRef.id}")
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(e.message ?: "Failed to submit report")
            }
        }
    }

    /**
     * Resets the report state back to Idle.
     */
    fun resetState() {
        _reportState.value = ReportState.Idle
    }
}
