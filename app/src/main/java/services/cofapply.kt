package services

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

/**
 * A screen for users to apply for a Certificate of Fitness (COF).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CofApplicationScreen(
    onBackClick: () -> Unit,
    onSubmitClick: (String, String) -> Unit
) {
    val context = LocalContext.current

    // State for all form fields
    var fullName by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var tin by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var physicalAddress by remember { mutableStateOf("") }
    var plateNumber by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var engineNumber by remember { mutableStateOf("") }
    var makeModel by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var insuranceProvider by remember { mutableStateOf("") }
    var policyNumber by remember { mutableStateOf("") }
    var policyDates by remember { mutableStateOf("") }
    var previousCof by remember { mutableStateOf("") }

    // State for dropdowns
    val vehicleTypes = remember { listOf("Private Saloon", "Goods Vehicle", "Public Service Vehicle/Minibus") }
    var selectedVehicleType by remember { mutableStateOf(vehicleTypes[0]) }
    var isVehicleDropdownExpanded by remember { mutableStateOf(false) }

    val applicationTypes = remember { listOf("COF Renewal", "First-Time COF") }
    var selectedAppType by remember { mutableStateOf(applicationTypes[0]) }
    var isAppTypeDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COF Application") },
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

            // --- Section Cards for organization ---
            item {
                FormSection("1. Owner's Details") {
                    OwnerDetailsFields(
                        fullName = fullName, onFullNameChange = { fullName = it },
                        nationalId = nationalId, onNationalIdChange = { nationalId = it },
                        tin = tin, onTinChange = { tin = it },
                        contactInfo = contactInfo, onContactInfoChange = { contactInfo = it },
                        physicalAddress = physicalAddress, onPhysicalAddressChange = { physicalAddress = it }
                    )
                }
            }
            item {
                FormSection("2. Vehicle's Core Details") {
                    VehicleDetailsFields(
                        plateNumber = plateNumber, onPlateNumberChange = { plateNumber = it },
                        vin = vin, onVinChange = { vin = it },
                        engineNumber = engineNumber, onEngineNumberChange = { engineNumber = it },
                        makeModel = makeModel, onMakeModelChange = { makeModel = it },
                        year = year, onYearChange = { year = it },
                        selectedVehicleType = selectedVehicleType, onVehicleTypeChange = { selectedVehicleType = it },
                        isDropdownExpanded = isVehicleDropdownExpanded, onDropdownExpand = { isVehicleDropdownExpanded = it },
                        vehicleTypes = vehicleTypes
                    )
                }
            }
            item {
                FormSection("3. Insurance Details") {
                    InsuranceDetailsFields(
                        provider = insuranceProvider, onProviderChange = { insuranceProvider = it },
                        policyNumber = policyNumber, onPolicyNumberChange = { policyNumber = it },
                        policyDates = policyDates, onPolicyDatesChange = { policyDates = it }
                    )
                }
            }
            item {
                FormSection("4. Application Type") {
                    ApplicationTypeFields(
                        selectedAppType = selectedAppType, onAppTypeChange = { selectedAppType = it },
                        isDropdownExpanded = isAppTypeDropdownExpanded, onDropdownExpand = { isAppTypeDropdownExpanded = it },
                        applicationTypes = applicationTypes,
                        previousCof = previousCof, onPreviousCofChange = { previousCof = it }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // TODO: Add form validation before submitting
                        Toast.makeText(context, "Application Submitted Successfully!", Toast.LENGTH_LONG).show()
                        onSubmitClick(plateNumber, makeModel)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("SUBMIT APPLICATION", style = MaterialTheme.typography.titleMedium)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSection(title: String, content: @Composable () -> Unit) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            content()
        }
    }
}

@Composable
private fun OwnerDetailsFields(
    fullName: String, onFullNameChange: (String) -> Unit,
    nationalId: String, onNationalIdChange: (String) -> Unit,
    tin: String, onTinChange: (String) -> Unit,
    contactInfo: String, onContactInfoChange: (String) -> Unit,
    physicalAddress: String, onPhysicalAddressChange: (String) -> Unit
) {
    OutlinedTextField(value = fullName, onValueChange = onFullNameChange, label = { Text("Full Legal Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
    OutlinedTextField(value = nationalId, onValueChange = onNationalIdChange, label = { Text("National ID Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = tin, onValueChange = onTinChange, label = { Text("TIN (Taxpayer ID)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = contactInfo, onValueChange = onContactInfoChange, label = { Text("Contact (Phone/Email)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = physicalAddress, onValueChange = onPhysicalAddressChange, label = { Text("Physical Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleDetailsFields(
    plateNumber: String, onPlateNumberChange: (String) -> Unit,
    vin: String, onVinChange: (String) -> Unit,
    engineNumber: String, onEngineNumberChange: (String) -> Unit,
    makeModel: String, onMakeModelChange: (String) -> Unit,
    year: String, onYearChange: (String) -> Unit,
    selectedVehicleType: String, onVehicleTypeChange: (String) -> Unit,
    isDropdownExpanded: Boolean, onDropdownExpand: (Boolean) -> Unit,
    vehicleTypes: List<String>
) {
    OutlinedTextField(value = plateNumber, onValueChange = onPlateNumberChange, label = { Text("Vehicle Registration Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters))
    OutlinedTextField(value = vin, onValueChange = onVinChange, label = { Text("VIN / Chassis Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = engineNumber, onValueChange = onEngineNumberChange, label = { Text("Engine Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = makeModel, onValueChange = onMakeModelChange, label = { Text("Make, Model, and Colour") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
    OutlinedTextField(value = year, onValueChange = onYearChange, label = { Text("Year of Manufacture") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = onDropdownExpand, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedVehicleType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Vehicle Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { onDropdownExpand(false) }) {
            vehicleTypes.forEach { type ->
                DropdownMenuItem(text = { Text(type) }, onClick = { onVehicleTypeChange(type); onDropdownExpand(false) })
            }
        }
    }
}

@Composable
private fun InsuranceDetailsFields(
    provider: String, onProviderChange: (String) -> Unit,
    policyNumber: String, onPolicyNumberChange: (String) -> Unit,
    policyDates: String, onPolicyDatesChange: (String) -> Unit
) {
    OutlinedTextField(value = provider, onValueChange = onProviderChange, label = { Text("Name of Insurance Provider") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words))
    OutlinedTextField(value = policyNumber, onValueChange = onPolicyNumberChange, label = { Text("Insurance Policy Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    OutlinedTextField(value = policyDates, onValueChange = onPolicyDatesChange, label = { Text("Policy Start and Expiry Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationTypeFields(
    selectedAppType: String, onAppTypeChange: (String) -> Unit,
    isDropdownExpanded: Boolean, onDropdownExpand: (Boolean) -> Unit,
    applicationTypes: List<String>,
    previousCof: String, onPreviousCofChange: (String) -> Unit
) {
    ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = onDropdownExpand, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedAppType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Application Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { onDropdownExpand(false) }) {
            applicationTypes.forEach { type ->
                DropdownMenuItem(text = { Text(type) }, onClick = { onAppTypeChange(type); onDropdownExpand(false) })
            }
        }
    }
    if (selectedAppType == "COF Renewal") {
        OutlinedTextField(value = previousCof, onValueChange = onPreviousCofChange, label = { Text("Previous COF Number (if applicable)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CofApplicationScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        CofApplicationScreen(onBackClick = {}, onSubmitClick = { _, _ -> })
    }
}
