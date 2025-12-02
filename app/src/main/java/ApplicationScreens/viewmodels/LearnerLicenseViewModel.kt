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
 * Data class to represent a Learner License application
 */
data class LearnerLicenseApplication(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val fullName: String = "",
    val nationalId: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val nationality: String = "",
    val physicalAddress: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val drivingSchoolId: String = "",
    val drivingSchoolName: String = "",
    val licenseCategory: String = "",
    val hasMedicalCondition: Boolean = false,
    val status: String = "Pending", // Pending, Approved, Rejected
    val timestamp: Any = FieldValue.serverTimestamp(),
    val createdAt: Long = System.currentTimeMillis()
)

sealed class LearnerLicenseState {
    object Idle : LearnerLicenseState()
    object Loading : LearnerLicenseState()
    data class Success(val message: String = "Success") : LearnerLicenseState()
    data class Error(val message: String) : LearnerLicenseState()
}

class LearnerLicenseViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val applicationsCollection = db.collection("learner_license_applications")

    private val _applicationState = MutableStateFlow<LearnerLicenseState>(LearnerLicenseState.Idle)
    val applicationState: StateFlow<LearnerLicenseState> = _applicationState

    /**
     * Submit learner license application to Firestore
     */
    fun submitApplication(
        fullName: String,
        nationalId: String,
        dateOfBirth: String,
        gender: String,
        nationality: String,
        physicalAddress: String,
        mobileNumber: String,
        email: String,
        drivingSchoolId: String,
        drivingSchoolName: String,
        licenseCategory: String,
        hasMedicalCondition: Boolean
    ) {
        viewModelScope.launch {
            _applicationState.value = LearnerLicenseState.Loading
            try {
                val user = auth.currentUser
                if (user == null) {
                    _applicationState.value = LearnerLicenseState.Error("User not authenticated")
                    return@launch
                }

                val application = hashMapOf(
                    "userId" to user.uid,
                    "userEmail" to (user.email ?: ""),
                    "fullName" to fullName,
                    "nationalId" to nationalId.uppercase(),
                    "dateOfBirth" to dateOfBirth,
                    "gender" to gender,
                    "nationality" to nationality,
                    "physicalAddress" to physicalAddress,
                    "mobileNumber" to mobileNumber,
                    "email" to email,
                    "drivingSchoolId" to drivingSchoolId,
                    "drivingSchoolName" to drivingSchoolName,
                    "licenseCategory" to licenseCategory,
                    "hasMedicalCondition" to hasMedicalCondition,
                    "status" to "Pending",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "createdAt" to System.currentTimeMillis()
                )

                val docRef = applicationsCollection.add(application).await()
                
                _applicationState.value = LearnerLicenseState.Success(
                    "Learner License Application submitted successfully! Application ID: ${docRef.id}"
                )
            } catch (e: Exception) {
                _applicationState.value = LearnerLicenseState.Error(
                    e.message ?: "Failed to submit application"
                )
            }
        }
    }

    /**
     * Fetch all learner license applications for the current user
     */
    fun fetchUserApplications() {
        viewModelScope.launch {
            _applicationState.value = LearnerLicenseState.Loading
            try {
                val user = auth.currentUser
                if (user == null) {
                    _applicationState.value = LearnerLicenseState.Error("User not authenticated")
                    return@launch
                }

                val snapshot = applicationsCollection
                    .whereEqualTo("userId", user.uid)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                _applicationState.value = LearnerLicenseState.Success(
                    "Found ${snapshot.size()} application(s)"
                )
            } catch (e: Exception) {
                _applicationState.value = LearnerLicenseState.Error(
                    e.message ?: "Failed to fetch applications"
                )
            }
        }
    }

    fun resetState() {
        _applicationState.value = LearnerLicenseState.Idle
    }
}
