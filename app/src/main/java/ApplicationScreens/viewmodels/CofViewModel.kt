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
 * Data class to represent a COF application.
 */
data class CofApplication(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val plateNumber: String = "",
    val vehicleModel: String = "",
    val chassisNumber: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val inspectionDate: String = "",
    val timestamp: Any = FieldValue.serverTimestamp(),
    val createdAt: Long = System.currentTimeMillis()
)

sealed class CofState {
    object Idle : CofState()
    object Loading : CofState()
    data class Success(val message: String = "Success") : CofState()
    data class Error(val message: String) : CofState()
}

class CofViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val cofCollection = db.collection("cof_applications")

    private val _cofState = MutableStateFlow<CofState>(CofState.Idle)
    val cofState: StateFlow<CofState> = _cofState

    private val _applicationStatus = MutableStateFlow<CofApplication?>(null)
    val applicationStatus: StateFlow<CofApplication?> = _applicationStatus

    /**
     * Submits a COF application to Firestore.
     */
    fun submitApplication(plateNumber: String, vehicleModel: String, chassisNumber: String) {
        viewModelScope.launch {
            _cofState.value = CofState.Loading
            try {
                val user = auth.currentUser
                if (user == null) {
                    _cofState.value = CofState.Error("User not authenticated")
                    return@launch
                }

                val application = hashMapOf(
                    "userId" to user.uid,
                    "userEmail" to (user.email ?: ""),
                    "plateNumber" to plateNumber.uppercase(),
                    "vehicleModel" to vehicleModel,
                    "chassisNumber" to chassisNumber.uppercase(),
                    "status" to "Pending",
                    "inspectionDate" to "",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "createdAt" to System.currentTimeMillis()
                )

                // Add to Firestore
                val docRef = cofCollection.add(application).await()
                
                _cofState.value = CofState.Success("Application submitted successfully with ID: ${docRef.id}")
            } catch (e: Exception) {
                _cofState.value = CofState.Error(e.message ?: "Failed to submit application")
            }
        }
    }

    /**
     * Fetches the status of a COF application by plate number.
     */
    fun fetchApplicationStatus(plateNumber: String) {
        viewModelScope.launch {
            _cofState.value = CofState.Loading
            try {
                val user = auth.currentUser
                if (user == null) {
                    _cofState.value = CofState.Error("User not authenticated")
                    return@launch
                }

                val cleanPlate = plateNumber.replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
                
                // Query Firestore for applications matching plate number and user
                val snapshot = cofCollection
                    .whereEqualTo("userId", user.uid)
                    .whereEqualTo("plateNumber", cleanPlate)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val application = CofApplication(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        userEmail = doc.getString("userEmail") ?: "",
                        plateNumber = doc.getString("plateNumber") ?: "",
                        vehicleModel = doc.getString("vehicleModel") ?: "",
                        chassisNumber = doc.getString("chassisNumber") ?: "",
                        status = doc.getString("status") ?: "Pending",
                        inspectionDate = doc.getString("inspectionDate") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L
                    )
                    _applicationStatus.value = application
                    _cofState.value = CofState.Success("Application found")
                } else {
                    _cofState.value = CofState.Error("No application found for this plate number.")
                }
            } catch (e: Exception) {
                _cofState.value = CofState.Error(e.message ?: "Failed to fetch status")
            }
        }
    }

    /**
     * Fetch all COF applications for the current user
     */
    fun fetchUserApplications() {
        viewModelScope.launch {
            _cofState.value = CofState.Loading
            try {
                val user = auth.currentUser
                if (user == null) {
                    _cofState.value = CofState.Error("User not authenticated")
                    return@launch
                }

                val snapshot = cofCollection
                    .whereEqualTo("userId", user.uid)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                // You can process the list here if needed
                _cofState.value = CofState.Success("Found ${snapshot.size()} applications")
            } catch (e: Exception) {
                _cofState.value = CofState.Error(e.message ?: "Failed to fetch applications")
            }
        }
    }

    fun resetState() {
        _cofState.value = CofState.Idle
    }
}
