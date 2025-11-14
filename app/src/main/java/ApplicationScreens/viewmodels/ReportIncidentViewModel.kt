package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class to represent an incident report.
 */
data class IncidentReport(
    val reporterName: String = "",
    val contactNumber: String = "",
    val incidentType: String = "",
    val location: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Sealed class to represent the state of the report submission.
 */
sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}

class ReportIncidentViewModel : ViewModel() {

    // Get an instance of the Realtime Database
    private val database = FirebaseDatabase.getInstance().getReference("reports")

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState

    /**
     * Submits an incident report to the Firebase Realtime Database.
     */
    fun sendReport(report: IncidentReport) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            try {
                // Create a unique key for the new report
                val reportId = database.push().key
                if (reportId != null) {
                    // Save the report object to the database under the new key
                    database.child(reportId).setValue(report).await()
                    _reportState.value = ReportState.Success
                } else {
                    _reportState.value = ReportState.Error("Could not generate a unique ID for the report.")
                }
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(e.message ?: "An unknown error occurred.")
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
