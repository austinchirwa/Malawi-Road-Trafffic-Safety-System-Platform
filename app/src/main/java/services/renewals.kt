package services

import ApplicationScreens.viewmodels.RenewalState
import ApplicationScreens.viewmodels.RenewalsViewModel
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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

/**
 * A screen for users to apply for the renewal of their driver's license and vehicle registration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenewalsScreen(
    viewModel: RenewalsViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }
    var licenseCode by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var vehicleRegNumber by remember { mutableStateOf("") }
    var trnNumber by remember { mutableStateOf("") }
    var insuranceProvider by remember { mutableStateOf("") }

    val context = LocalContext.current
    val renewalState by viewModel.renewalState.collectAsState()

    LaunchedEffect(renewalState) {
        when (val state = renewalState) {
            is RenewalState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
                onNavigateToPayment()
            }
            is RenewalState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit // Idle or Loading
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver and Vehicle Renewal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Driver Details Section
                item {
                    Text("Driver Details", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                }
                item {
                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = { idNumber = it },
                        label = { Text("ID Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                item {
                    OutlinedTextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = { Text("Driver's License Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )
                }
                item {
                    OutlinedTextField(
                        value = licenseCode,
                        onValueChange = { licenseCode = it },
                        label = { Text("Code of License") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )
                }
                item {
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Date of Birth (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Vehicle Details Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Vehicle Details", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    OutlinedTextField(
                        value = vehicleRegNumber,
                        onValueChange = { vehicleRegNumber = it },
                        label = { Text("Vehicle Registration Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )
                }
                item {
                    OutlinedTextField(
                        value = trnNumber,
                        onValueChange = { trnNumber = it },
                        label = { Text("TRN (Traffic Registration Number)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )
                }
                item {
                    OutlinedTextField(
                        value = insuranceProvider,
                        onValueChange = { insuranceProvider = it },
                        label = { Text("Insurance Provider") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                }

                // Apply Button
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val userId = viewModel.getCurrentUserId()
                            if (userId == null) {
                                Toast.makeText(context, "You must be logged in to apply.", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            if (fullName.isNotBlank() && idNumber.isNotBlank() && licenseNumber.isNotBlank() && licenseCode.isNotBlank() && vehicleRegNumber.isNotBlank() && trnNumber.isNotBlank()) {
                                viewModel.submitApplication(
                                    fullName = fullName,
                                    idNumber = idNumber,
                                    licenseNumber = licenseNumber,
                                    licenseCode = licenseCode,
                                    dateOfBirth = dateOfBirth,
                                    vehicleRegNumber = vehicleRegNumber,
                                    trnNumber = trnNumber,
                                    insuranceProvider = insuranceProvider
                                )
                            } else {
                                Toast.makeText(context, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = renewalState != RenewalState.Loading
                    ) {
                        Text("APPLY FOR RENEWAL", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            if (renewalState == RenewalState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RenewalsScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        RenewalsScreen(onBackClick = {}, onNavigateToPayment = {})
    }
}
