package services

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CofDashboardScreen(
    plateNumber: String,
    vehicleModel: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var paymentStatus by remember { mutableStateOf("Pending Payment") }
    val refNumber = remember { "COF-2025-${(10000..99999).random()}" }

    val isPaymentConfirmed = paymentStatus == "Payment Confirmed"

    // Decode the URL-encoded parameters passed from the previous screen
    val decodedPlateNumber = remember(plateNumber) { URLDecoder.decode(plateNumber, StandardCharsets.UTF_8.toString()) }
    val decodedVehicleModel = remember(vehicleModel) { URLDecoder.decode(vehicleModel, StandardCharsets.UTF_8.toString()) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Dashboard") },
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
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ApplicationSummaryCard(
                    status = paymentStatus,
                    refNumber = refNumber,
                    plateNumber = decodedPlateNumber,
                    vehicleModel = decodedVehicleModel
                )
            }

            item {
                PaymentStepCard(
                    isPaymentConfirmed = isPaymentConfirmed,
                    onConfirmPayment = {
                        paymentStatus = "Payment Confirmed"
                        Toast.makeText(context, "Payment successful!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            item {
                PlaceholderStepCard(
                    title = "Step 2: Schedule Your Inspection",
                    description = "Once payment is confirmed, you will be able to book a time slot for your vehicle inspection at your nearest DRTSS station.",
                    enabled = isPaymentConfirmed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationSummaryCard(status: String, refNumber: String, plateNumber: String, vehicleModel: String) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Application Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            HorizontalDivider()
            InfoRow("Application Status:", status, if (status == "Payment Confirmed") Color(0xFF008000) else Color.Red)
            InfoRow("Reference Number:", refNumber)
            InfoRow("Vehicle:", "$plateNumber - $vehicleModel")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentStepCard(isPaymentConfirmed: Boolean, onConfirmPayment: () -> Unit) {
    val context = LocalContext.current

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = if (isPaymentConfirmed) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Step 1: Secure Payment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            if (isPaymentConfirmed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, "Success", tint = Color(0xFF008000))
                    Spacer(Modifier.padding(horizontal = 8.dp))
                    Text("Payment Confirmed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            } else {
                Text("COF Examination Fee: MK 30,000", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))

                var selectedTabIndex by remember { mutableIntStateOf(0) }
                val tabs = listOf("Mobile Money", "Bank/Card")
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                            Text(title, modifier = Modifier.padding(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (selectedTabIndex == 0) { // Mobile Money
                    var phoneNumber by remember { mutableStateOf("") }
                    OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("TNM Mpamba or Airtel Money Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                } else { // Card
                    Text("Card payment is not yet implemented.")
                }

                Spacer(Modifier.height(16.dp))
                Button(onClick = onConfirmPayment, modifier = Modifier.fillMaxWidth()) {
                    Text("PAY NOW")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderStepCard(title: String, description: String, enabled: Boolean) {
    Card(elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (enabled) Color.Black else Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(description, color = if (enabled) Color.Black else Color.Gray)
            if (!enabled) {
                Spacer(Modifier.height(8.dp))
                Text("(Complete previous steps to unlock)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row {
        Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.5f))
        Text(value, modifier = Modifier.weight(0.5f), color = valueColor)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CofDashboardScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        CofDashboardScreen(plateNumber = "NE 4141", vehicleModel = "Toyota Prado, Silver", onBackClick = {})
    }
}
