package services

import ApplicationScreens.utils.LocationHelper
import ApplicationScreens.utils.LocationResult
import ApplicationScreens.utils.PermissionUtils
import ApplicationScreens.viewmodels.IncidentReport
import ApplicationScreens.viewmodels.ReportIncidentViewModel
import ApplicationScreens.viewmodels.ReportState
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidentScreen(
    viewModel: ReportIncidentViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val reportState by viewModel.reportState.collectAsState()
    val scope = rememberCoroutineScope()

    var reporterName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var incidentLocation by remember { mutableStateOf("") }
    var incidentDescription by remember { mutableStateOf("") }
    var isLoadingLocation by remember { mutableStateOf(false) }

    val incidentTypes = remember { listOf("Accident", "Road Hazard", "Traffic Violation", "Broken Down Vehicle", "Other") }
    var selectedIncidentType by remember { mutableStateOf(incidentTypes[0]) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Location helper
    val locationHelper = remember { LocationHelper(context) }
    
    // Permission launcher for location
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            // Permission granted, get location
            scope.launch {
                isLoadingLocation = true
                when (val result = locationHelper.getCurrentLocation()) {
                    is LocationResult.Success -> {
                        incidentLocation = result.formattedAddress
                        Toast.makeText(context, "Location obtained", Toast.LENGTH_SHORT).show()
                    }
                    is LocationResult.Error -> {
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
                isLoadingLocation = false
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of the report submission
    LaunchedEffect(reportState) {
        when (val state = reportState) {
            is ReportState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onBackClick()
            }
            is ReportState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit // Idle or Loading
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Title removed */ },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item { SectionHeader("Your Details") }
                item { OutlinedTextField(value = reporterName, onValueChange = { reporterName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Full Name") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words), singleLine = true) }
                item { OutlinedTextField(value = contactNumber, onValueChange = { contactNumber = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Contact Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true) }

                item { SectionHeader("Incident Details") }
                item {
                    ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = selectedIncidentType, onValueChange = {}, readOnly = true, label = { Text("Type of Incident") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                        ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                            incidentTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { selectedIncidentType = type; isDropdownExpanded = false })
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = incidentLocation,
                        onValueChange = { incidentLocation = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Accident Location (e.g., road, nearest landmark)") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    // Check and request location permission
                                    if (PermissionUtils.isLocationPermissionGranted(context)) {
                                        scope.launch {
                                            isLoadingLocation = true
                                            when (val result = locationHelper.getCurrentLocation()) {
                                                is LocationResult.Success -> {
                                                    incidentLocation = result.formattedAddress
                                                    Toast.makeText(context, "Location obtained", Toast.LENGTH_SHORT).show()
                                                }
                                                is LocationResult.Error -> {
                                                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            isLoadingLocation = false
                                        }
                                    } else {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                PermissionUtils.LOCATION_FINE,
                                                PermissionUtils.LOCATION_COARSE
                                            )
                                        )
                                    }
                                },
                                enabled = !isLoadingLocation
                            ) {
                                if (isLoadingLocation) {
                                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                                } else {
                                    Icon(Icons.Default.GpsFixed, "Get current location")
                                }
                            }
                        }
                    )
                }
                item { OutlinedTextField(value = incidentDescription, onValueChange = { incidentDescription = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Brief Description") }, keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)) }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val report = IncidentReport(reporterName, contactNumber, selectedIncidentType, incidentLocation, incidentDescription)
                            viewModel.sendReport(report)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = reportState != ReportState.Loading
                    ) {
                        Text("SEND REPORT", style = MaterialTheme.typography.titleMedium)
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            if (reportState == ReportState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportIncidentScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        ReportIncidentScreen(onBackClick = {})
    }
}
