package ApplicationScreens

import ApplicationScreens.viewmodels.DrivingSchool
import ApplicationScreens.viewmodels.DrivingSchoolViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnerLicenseScreen(
    drivingSchoolViewModel: DrivingSchoolViewModel = viewModel(),
    onApplyClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // --- STATE MANAGEMENT ---
    var fullName by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var physicalAddress by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Dropdown states
    val genders = remember { listOf("Male", "Female", "Other") }
    var selectedGender by remember { mutableStateOf(genders[0]) }
    var isGenderDropdownExpanded by remember { mutableStateOf(false) }

    val drivingSchools by drivingSchoolViewModel.schools.collectAsState()
    var selectedSchool by remember { mutableStateOf<DrivingSchool?>(null) }
    var isSchoolDropdownExpanded by remember { mutableStateOf(false) }

    val licenseCategories = remember { listOf("Select Category", "Code A/A1 (Motorcycles)", "Code B/EB (Light Vehicles)", "Code C/C1/EC (Heavy Vehicles)") }
    var selectedCategory by remember { mutableStateOf(licenseCategories[0]) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // Medical Declaration State
    var hasMedicalCondition by remember { mutableStateOf<Boolean?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learner License Application") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // --- Section 1: Personal & Identity Details ---
            item {
                FormSection(title = "1. Personal & Identity Details") {
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Legal Name") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
                    OutlinedTextField(value = nationalId, onValueChange = { nationalId = it }, label = { Text("National ID Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = { Text("Date of Birth (DD/MM/YYYY)") }, modifier = Modifier.fillMaxWidth())

                    // Gender Dropdown
                    ExposedDropdownMenuBox(expanded = isGenderDropdownExpanded, onExpandedChange = { isGenderDropdownExpanded = it }) {
                        OutlinedTextField(
                            value = selectedGender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sex/Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = isGenderDropdownExpanded, onDismissRequest = { isGenderDropdownExpanded = false }) {
                            genders.forEach { gender ->
                                DropdownMenuItem(text = { Text(gender) }, onClick = { selectedGender = gender; isGenderDropdownExpanded = false })
                            }
                        }
                    }

                    OutlinedTextField(value = nationality, onValueChange = { nationality = it }, label = { Text("Nationality") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
                    OutlinedTextField(value = physicalAddress, onValueChange = { physicalAddress = it }, label = { Text("Physical Address") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
                }
            }

            // --- Section 2: Contact Information ---
            item {
                FormSection(title = "2. Contact Information") {
                    OutlinedTextField(value = mobileNumber, onValueChange = { mobileNumber = it }, label = { Text("Mobile Phone Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }
            }

            // --- Section 3: Application Specifics ---
            item {
                FormSection(title = "3. Application Specifics") {
                    // Driving School Dropdown
                    ExposedDropdownMenuBox(expanded = isSchoolDropdownExpanded, onExpandedChange = { isSchoolDropdownExpanded = it }) {
                        OutlinedTextField(
                            value = selectedSchool?.name ?: "Select Driving School",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Accredited Driving School") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSchoolDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = isSchoolDropdownExpanded, onDismissRequest = { isSchoolDropdownExpanded = false }) {
                            drivingSchools.forEach { school ->
                                DropdownMenuItem(text = { Text(school.name) }, onClick = { selectedSchool = school; isSchoolDropdownExpanded = false })
                            }
                        }
                    }

                    // License Category Dropdown
                    ExposedDropdownMenuBox(expanded = isCategoryDropdownExpanded, onExpandedChange = { isCategoryDropdownExpanded = it }) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("License Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = isCategoryDropdownExpanded, onDismissRequest = { isCategoryDropdownExpanded = false }) {
                            licenseCategories.forEach { category ->
                                DropdownMenuItem(text = { Text(category) }, onClick = { selectedCategory = category; isCategoryDropdownExpanded = false })
                            }
                        }
                    }
                }
            }

            // --- Section 4: Medical Declaration ---
            item {
                FormSection(title = "4. Medical Declaration") {
                    Text("Do you have any physical disability or medical condition that may affect your fitness to drive?", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = hasMedicalCondition == true,
                                onClick = { hasMedicalCondition = true }
                            )
                            Text("Yes")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = hasMedicalCondition == false,
                                onClick = { hasMedicalCondition = false }
                            )
                            Text("No")
                        }
                    }
                }
            }

            // --- Submit Button ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // TODO: Add form validation & save application to Firestore
                        Toast.makeText(context, "Learner License Application Submitted!", Toast.LENGTH_LONG).show()
                        onApplyClick()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("SUBMIT APPLICATION", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            content()
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LearnerLicenseScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        LearnerLicenseScreen(onApplyClick = {}, onBackClick = {})
    }
}
